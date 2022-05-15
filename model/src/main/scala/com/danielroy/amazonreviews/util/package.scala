package com.danielroy.amazonreviews

import eu.timepit.refined
import eu.timepit.refined.{W, refineV}
import eu.timepit.refined.api.{Refined, RefinedTypeOps}
import eu.timepit.refined.numeric.{Interval, NonNegative, Positive}
import eu.timepit.refined.string.MatchesRegex
import eu.timepit.refined.types.all.{NonEmptyString, NonNegDouble, NonNegLong}
import zio.{IO, ZIO}
import zio.json.{JsonDecoder, JsonEncoder}

package object util {

  sealed case class HelpfulRating(low: Int Refined NonNegative, high: Int Refined NonNegative)

  implicit val encoderHelpfulRating: JsonEncoder[HelpfulRating] =
    JsonEncoder[Array[Int]].contramap(i => Array(i.low.value, i.high.value))
  implicit val decoderHelpfulRating: JsonDecoder[HelpfulRating] =
    JsonDecoder[Array[Int]].mapOrFail { array =>
      if (array.length != 2) Left("Helpful Rating must consist of exactly 2 non-negative integer values")
      else {
        val (low, high) = (array.head, array(1))
        HelpfulRating.fromInts(low, high)
      }
    }

  object HelpfulRating {
    private def verifyInputs(low: Int, high: Int): Either[String, Unit] = {
      if (low > high) Left("The first value for Helpful Rating must be <= than the second")
      else Right(())
    }
    def fromInts(low: Int, high: Int): Either[String, HelpfulRating] =
      for {
        _ <- verifyInputs(low, high)
        l <- refineV[NonNegative](low)
        h <- refineV[NonNegative](high)
      } yield HelpfulRating(l, h)

    def fromIntsZIO(low: Int, high: Int): IO[String, HelpfulRating] =
      ZIO.fromEither(fromInts(low, high))
  }

  implicit val encoderNonNegative: JsonEncoder[Int Refined NonNegative] =
    JsonEncoder[Int].contramap(_.value)
  implicit val decoderNonNegative: JsonDecoder[Int Refined NonNegative] =
    JsonDecoder[Int].mapOrFail(refineV[NonNegative](_))

  implicit val encoderPositive: JsonEncoder[Int Refined Positive] =
    JsonEncoder[Int].contramap(_.value)
  implicit val decoderPositive: JsonDecoder[Int Refined Positive] =
    JsonDecoder[Int].mapOrFail(refineV[Positive](_))

  type ProductRating = Int Refined Interval.Closed[1, 5]
  implicit val encoderProductRating: JsonEncoder[ProductRating] =
    JsonEncoder[Int].contramap[ProductRating](_.value)
  implicit val decoderProductRating: JsonDecoder[ProductRating] =
    JsonDecoder[Int].mapOrFail(refined.refineV[Interval.Closed[1, 5]](_))

  object ProductRating extends RefinedTypeOps[ProductRating, Int] {
    def fromIntZIO(t: Int): IO[String, ProductRating] = ZIO.fromEither(super.from(t))
  }

  implicit val encoderNonEmptyString: JsonEncoder[NonEmptyString] =
    JsonEncoder[String].contramap(_.value)
  implicit val decoderNonEmptyString: JsonDecoder[NonEmptyString] =
    JsonDecoder[String].mapOrFail(NonEmptyString.from)

  object NonEmptyStringHelper {
    def fromStringZIO(str: String): IO[String, NonEmptyString] = ZIO.fromEither(NonEmptyString.from(str))
  }

  implicit val encoderNonNegLong: JsonEncoder[NonNegLong] =
    JsonEncoder[Long].contramap(_.value)
  implicit val decoderNonNegLong: JsonDecoder[NonNegLong] =
    JsonDecoder[Long].mapOrFail(NonNegLong.from)

  object NonNegLongHelper {
    def fromLongZIO(l: Long): IO[String, NonNegLong] = ZIO.fromEither(NonNegLong.from(l))
  }

  type SimpleDate = String Refined MatchesRegex[W.`"""(0[1-9]|[12][0-9]|3[01])\\.(0[1-9]|1[0-2])\\.\\d{4}"""`.T]
  implicit val encoderSimpleDate: JsonEncoder[SimpleDate] =
    JsonEncoder[String].contramap(_.value)
  implicit val decoderSimpleDate: JsonDecoder[SimpleDate] =
    JsonDecoder[String].mapOrFail(SimpleDate.from)

  object SimpleDate extends RefinedTypeOps[SimpleDate, String] {
    def fromStringZIO(str: String): IO[String, SimpleDate] = ZIO.fromEither(super.from(str))
  }

  implicit val encoderNonNegDouble: JsonEncoder[NonNegDouble] =
    JsonEncoder[Double].contramap(_.value)
  implicit val decoderNonNegDouble: JsonDecoder[NonNegDouble] =
    JsonDecoder[Double].mapOrFail(NonNegDouble.from)

  object NonNegDoubleHelper {
    def fromDoubleZIO(d: Double): IO[String, NonNegDouble] = ZIO.fromEither(NonNegDouble.from(d))
  }
}
