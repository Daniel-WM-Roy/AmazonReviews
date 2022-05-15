package com.danielroy.amazonreviews.util

import zio.Scope
import zio.test.Assertion._
import zio.test.{Spec, TestEnvironment, ZIOSpecDefault, assertTrue}

object EncoderTests extends ZIOSpecDefault {

  // Tests for other types omitted for brevity

  override def spec: Spec[TestEnvironment with Scope, Any] =
    suite("Tests of the various JSON encoders and decoders inside the util package")(
      test("SimpleDate encoder test, valid input") {
        for {
          sd <- SimpleDate.fromStringZIO("01.01.2010")
        } yield assertTrue(sd.value == "01.01.2010")
      },
      test("SimpleDate encoder test, invalid input, dashes instead of periods") {
        for {
          sd <- SimpleDate.fromStringZIO("01-01-2010").exit
        } yield smartAssert(sd)(failsWithA[String])
      },
      test("ProductRating encoder test, valid input") {
        for {
          pr <- ProductRating.fromIntZIO(3)
        } yield assertTrue(pr.value == 3)
      },
      test("ProductRating encoder test, invalid input, negative number") {
        for {
          pr <- ProductRating.fromIntZIO(-5).exit
        } yield smartAssert(pr)(failsWithA[String])
      },
      test("HelpfulRating encoder test, valid input") {
        for {
          hr <- HelpfulRating.fromIntsZIO(15, 27)
        } yield assertTrue(
          hr.low.value == 15,
          hr.high.value == 27
        )
      },
      test("HelpfulRating encoder test, invalid input, negative number") {
        for {
          hr <- HelpfulRating.fromIntsZIO(-5, 12).exit
        } yield smartAssert(hr)(failsWithA[String])
      },
      test("HelpfulRating encoder test, invalid input, first number is higher than second") {
        for {
          hr <- HelpfulRating.fromIntsZIO(15, 3).exit
        } yield smartAssert(hr)(failsWithA[String])
      }
    )
}
