package uk.gov.bis.levyApiMock.data.oauth2

import org.scalatest.{Matchers, OptionValues, WordSpecLike}

class AuthRecordTest extends WordSpecLike with Matchers with OptionValues {

  "AuthRecord" should {
    "say the refresh token is expired when there is no refreshedAt time" in {
      val now = System.currentTimeMillis()
      val ar = AuthRecord("at", Some("rt"), None, "", None, 3600, now, "", None)

      ar.refreshTokenExpired(now + ar.eighteenMonths + 1) shouldBe true
    }

    "say the refresh token is expired when there is a refreshedAt time" in {
      val now = System.currentTimeMillis()
      val ar = AuthRecord("at", Some("rt"), Some(now), "", None, 3600, now, "", None)

      ar.refreshTokenExpired(now + ar.eighteenMonths + 1) shouldBe true
    }

    "say the refresh token is not expired when there is no refreshedAt time" in {
      val now = System.currentTimeMillis()
      val ar = AuthRecord("at", Some("rt"), None, "", None, 3600, now, "", None)

      ar.refreshTokenExpired(now) shouldBe false
    }

    "say the refresh token is not expired when there is a refreshedAt time" in {
      val now = System.currentTimeMillis()
      val ar = AuthRecord("at", Some("rt"), Some(now), "", None, 3600, now, "", None)

      ar.refreshTokenExpired(now) shouldBe false
    }
  }

}
