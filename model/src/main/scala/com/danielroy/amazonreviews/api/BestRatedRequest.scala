package com.danielroy.amazonreviews.api

import com.danielroy.amazonreviews.exceptions.EncodingException
import com.danielroy.amazonreviews.util.SimpleDate
import eu.timepit.refined.api.Refined
import eu.timepit.refined.numeric.Positive
import eu.timepit.refined.refineV
import zio.json.{DeriveJsonDecoder, DeriveJsonEncoder, JsonDecoder, JsonEncoder, jsonField}
import zio.{IO, ZIO}

case class BestRatedRequest(
    start: SimpleDate,
    end: SimpleDate,
    limit: Int Refined Positive,
    @jsonField("min_number_reviews") minNumberReviews: Int Refined Positive
)

object BestRatedRequest {
  object JsonCoders {
    import com.danielroy.amazonreviews.util.{encoderSimpleDate, decoderSimpleDate, encoderPositive, decoderPositive, encoderNonNegDouble, decoderNonNegDouble}
    implicit val encoderBestRatedRequest: JsonEncoder[BestRatedRequest] = DeriveJsonEncoder.gen
    implicit val decoderBestRatedRequest: JsonDecoder[BestRatedRequest] = DeriveJsonDecoder.gen
  }

  def make(
      start: String,
      end: String,
      limit: Int,
      minNumberReviews: Int
  ): IO[EncodingException, BestRatedRequest] =
    (for {
      s <- SimpleDate.fromStringZIO(start)
      e <- SimpleDate.fromStringZIO(end)
      l <- ZIO.fromEither(refineV[Positive](limit))
      m <- ZIO.fromEither(refineV[Positive](minNumberReviews))
    } yield BestRatedRequest(s, e, l, m))
      .mapError(errMsg => EncodingException(errMsg))
}
