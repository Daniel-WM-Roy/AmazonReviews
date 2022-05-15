package com.danielroy.amazonreviews

package object exceptions {

  sealed trait AmazonReviewAPIException
  final case class EncodingException(errorMessage: String) extends AmazonReviewAPIException

}
