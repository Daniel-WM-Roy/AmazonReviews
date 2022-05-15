package com.danielroy.amazonreviews.api

import BestRatedRequest.JsonCoders._
import zio.{Scope, ZIO}
import zio.json.{DecoderOps, EncoderOps}
import zio.test.Assertion.{failsWithA, smartAssert}
import zio.test.{Spec, TestEnvironment, ZIOSpecDefault, assertTrue}

object BestRatedRequestSpec extends ZIOSpecDefault {

  private val bestRatedRequest1JSON =
    """{
      |  "start": "01.01.2010",
      |  "end": "31.12.2020",
      |  "limit": 2,
      |  "min_number_reviews": 2
      |}""".stripMargin

  override def spec: Spec[TestEnvironment with Scope, Any] =
    suite("BestRatedRequest specification")(
      test("Encoder and decoder test, valid input") {
        for {
          encoded <- ZIO.fromEither(bestRatedRequest1JSON.fromJson[BestRatedRequest])
          decoded <- ZIO.succeed(encoded.toJson)
          recoded <- ZIO.fromEither(decoded.fromJson[BestRatedRequest])
        } yield assertTrue(
          encoded.start.value == "01.01.2010",
          encoded.end.value == "31.12.2020",
          encoded.limit.value == 2,
          encoded.minNumberReviews.value == 2,

          encoded == recoded
        )
      },
      test("Encoder test, invalid JSON input") {
        for {
          encoded <- ZIO.fromEither("""{"start": "01.01.2010", "end": "31.12.2020",]""".fromJson[BestRatedRequest]).exit
        } yield smartAssert(encoded)(failsWithA[String])
      }
    )
}
