package uk.gov.bis.levyApiMock.controllers.auth

import org.scalatest.{FlatSpec, Matchers}

class AccessTokenControllerTest extends FlatSpec with Matchers {

  "processScopes" should "leave the scopes unchanged if 'openid' is not present" in {
    val scopes: List[String] = List("a", "b", "c")
    AccessTokenController.processScopes(scopes) shouldBe scopes
  }

  it should "drop the 'openid' scope and convert recognized scopes if 'openid' is present" in {
    val scopes = List("unrecognized", "openid", "profile", "taxids")
    val expectedScopes = List("unrecognized", "openid:profile", "openid:taxids")

    AccessTokenController.processScopes(scopes) shouldBe expectedScopes

  }
}
