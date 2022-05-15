package com.danielroy.amazonreviews.productreviews

import com.danielroy.amazonreviews.exceptions.EncodingException
import com.danielroy.amazonreviews.util._
import eu.timepit.refined.types.all.{NonEmptyString, NonNegDouble, NonNegLong}
import zio.json.{DeriveJsonDecoder, DeriveJsonEncoder, JsonDecoder, JsonEncoder}
import zio.{IO, ZIO}

final case class AmazonReview(
    reviewerID: NonEmptyString,
    asin: NonEmptyString,
    reviewerName: Option[NonEmptyString],
    helpful: HelpfulRating,
    reviewText: NonEmptyString,
    overall: NonNegDouble,
    summary: NonEmptyString,
    unixReviewTime: NonNegLong
) extends ProductReview

object AmazonReview {

  object JsonCoders {
    import com.danielroy.amazonreviews.util.{encoderNonNegDouble, decoderNonNegDouble}
    implicit val encoderAmazonReview: JsonEncoder[AmazonReview] = DeriveJsonEncoder.gen
    implicit val decoderAmazonReview: JsonDecoder[AmazonReview] = DeriveJsonDecoder.gen
  }

  def make(reviewerID: String, asin: String, reviewerName: Option[String], helpful: (Int, Int), reviewText: String, overall: Double, summary: String, unixReviewTime: Long)
      : IO[EncodingException, AmazonReview] = {
    (for {
      rid <- NonEmptyStringHelper.fromStringZIO(reviewerID)
      a   <- NonEmptyStringHelper.fromStringZIO(asin)
      rn <- reviewerName match {
        case Some(value) => NonEmptyStringHelper.fromStringZIO(value).map(Option.apply)
        case None        => ZIO.none
      }
      h  <- HelpfulRating.fromIntsZIO(helpful._1, helpful._2)
      rt <- NonEmptyStringHelper.fromStringZIO(reviewText)
      o  <- NonNegDoubleHelper.fromDoubleZIO(overall)
      s  <- NonEmptyStringHelper.fromStringZIO(summary)
      u  <- NonNegLongHelper.fromLongZIO(unixReviewTime)
    } yield AmazonReview(rid, a, rn, h, rt, o, s, u))
      .mapError(errMsg => EncodingException(errMsg))
  }

}
