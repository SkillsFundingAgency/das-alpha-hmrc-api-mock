package uk.gov.bis.levyApiMock.data.oauth2

import org.scalatest.{Matchers, OptionValues, WordSpecLike}
import uk.gov.bis.oauth.data.AuthRecord

class AuthRecordTest extends WordSpecLike with Matchers with OptionValues {
  val now = 300000L
  val arWithoutRefreshedAt = AuthRecord("at", Some("rt"), None, "", None, 3600, now, "", None)
  val arWithRefreshedAt = arWithoutRefreshedAt.copy(refreshedAt = Some(now))

  "AuthRecord" should {
    "say the refresh token is expired when there is no refreshedAt time" in {
      arWithoutRefreshedAt.refreshTokenExpired(now + arWithoutRefreshedAt.eighteenMonths + 1) shouldBe true
    }

    "say the refresh token is expired when there is a refreshedAt time" in {
      arWithRefreshedAt.refreshTokenExpired(now + arWithoutRefreshedAt.eighteenMonths + 1) shouldBe true
    }

    "say the refresh token is not expired when there is no refreshedAt time" in {
      arWithoutRefreshedAt.refreshTokenExpired(now) shouldBe false
    }

    "say the refresh token is not expired when there is a refreshedAt time" in {
      arWithRefreshedAt.refreshTokenExpired(now) shouldBe false
    }
  }

}
