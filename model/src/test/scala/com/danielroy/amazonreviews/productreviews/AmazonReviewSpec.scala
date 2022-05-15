package com.danielroy.amazonreviews.productreviews

import com.danielroy.amazonreviews.productreviews.AmazonReview.JsonCoders._
import zio.json.{DecoderOps, EncoderOps}
import zio.test.{Spec, TestEnvironment, TestResult, ZIOSpecDefault, assertTrue}
import zio.{Scope, ZIO}

object AmazonReviewSpec extends ZIOSpecDefault {

  // Tests for other scenarios omitted for brevity

  private val testAmazonReview1JSON =
    """
      |{"asin":"B000Q75VCO","helpful":[16,40],"overall":2.0,
      |"reviewText":"Words are in...",
      |"reviewerID":"B07844AAA04E4","reviewerName":"Gaylord Bashirian","summary":"Ut des...",
      |"unixReviewTime":1475261866}""".stripMargin.trim.replace("\n", "")

  private val testAmazonReview2JSON =
    """
      |{"asin":"B000NI7RW8","helpful":[32,52],"overall":3.0,
      |"reviewText":"Just because you have the emotional range of a teaspoon doesn’t mean we all have.",
      |"reviewerID":"4E82CF3A24D34",
      |"summary":"Debitis at facere minus animi quos sed.","unixReviewTime":1455120950}
      |""".stripMargin

  override def spec: Spec[TestEnvironment with Scope, Any] =
    suite("AmazonReview case class, companion object spec")(
      test("Encode from valid JSON, and then decode") {
        for {
          encoded <- ZIO.fromEither(testAmazonReview1JSON.fromJson[AmazonReview])
          decoded <- ZIO.succeed(encoded.toJson)
          recoded <- ZIO.fromEither(decoded.fromJson[AmazonReview])
        } yield {
          verifyAmazonReview(encoded)
          assertTrue(encoded == recoded)
        }
      },
      test("Encode from valid JSON, and then decode, no reviewer name") {
        for {
          encoded <- ZIO.fromEither(testAmazonReview2JSON.fromJson[AmazonReview])
          decoded <- ZIO.succeed(encoded.toJson)
          recoded <- ZIO.fromEither(decoded.fromJson[AmazonReview])
        } yield assertTrue(
          encoded.reviewerName.isEmpty,
          encoded == recoded
        )
      },
      test("AmazonReview companion object make function") {
        for {
          review <- AmazonReview.make("B07844AAA04E4", "B000Q75VCO", Some("Gaylord Bashirian"), (16, 40), "Words are in...", 2.0, "Ut des...", 1475261866L)
        } yield verifyAmazonReview(review)
      }
    )

  private def verifyAmazonReview(toVerify: AmazonReview): TestResult =
    assertTrue(
      toVerify.reviewerID.value == "B07844AAA04E4",
      toVerify.asin.value == "B000Q75VCO",
      toVerify.reviewerName.map(_.value) == Option("Gaylord Bashirian"),
      toVerify.helpful.low.value == 16,
      toVerify.helpful.high.value == 40,
      toVerify.reviewText.value == "Words are in...",
      toVerify.overall.value == 2.0,
      toVerify.summary.value == "Ut des...",
      toVerify.unixReviewTime.value == 1475261866L
    )
}
