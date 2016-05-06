package uk.gov.bis.api

import play.api.test.FakeRequest

object TestRequests {

  import TestData._

  val requestWithoutAuthorization = FakeRequest()
  val requestWithBasicAuth = FakeRequest().withHeaders("Authorization" -> "Basic abcdefg")
  val requestWithMatchingBearer = FakeRequest().withHeaders("Authorization" -> s"Bearer $validToken")
  val requestWithNonMatchingBearer = FakeRequest().withHeaders("Authorization" -> s"Bearer $invalidToken")
}
