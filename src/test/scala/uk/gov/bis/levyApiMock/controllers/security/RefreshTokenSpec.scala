package uk.gov.bis.levyApiMock.controllers.security

import org.scalatest.{Matchers, OptionValues, WordSpecLike}
import play.api.libs.json.{JsString, JsValue}
import play.api.mvc.Result
import play.api.test.FakeRequest
import play.api.test.Helpers._
import uk.gov.bis.levyApiMock.auth.APIDataHandler
import uk.gov.bis.levyApiMock.data.SystemTimeSource
import uk.gov.bis.levyApiMock.data.oauth2.AuthRecordOps

import scala.concurrent.Future


class RefreshTokenSpec extends WordSpecLike with Matchers with OptionValues {
  implicit val ec = scala.concurrent.ExecutionContext.Implicits.global

  import RefreshTokenSpecTestData._

  "accessToken" can {
    "handle a valid refresh_token request" should {
      val mockAuthRecords = new MockAuthRecords
      val result = accessToken(validRequest, mockAuthRecords)

      "return an OK status" in {
        status(result) shouldBe OK
      }

      val resultMap = contentAsJson(result).validate[Map[String, JsValue]].asOpt.value
      val deleteAuthRecord = mockAuthRecords.deletedAuthRecord.value
      val createdAuthRecord = mockAuthRecords.createdAuthRecord.value

      "return json containing the newly created access and refresh tokens" in {
        resultMap.get("access_token").value shouldBe JsString(createdAuthRecord.accessToken)
        resultMap.get("refresh_token").value shouldBe JsString(createdAuthRecord.refreshToken.value)
      }

      "delete the auth record with the supplied refresh token" in {
        deleteAuthRecord.refreshToken.value shouldBe refreshtoken1
      }

      "create a new record with the same user id and client id as the old record" in {
        createdAuthRecord.clientID shouldBe clientid1
        createdAuthRecord.gatewayID shouldBe user1
      }

      "create new values for the access and refresh tokens" in {
        createdAuthRecord.accessToken shouldNot be(deleteAuthRecord.accessToken)
        createdAuthRecord.refreshToken.value shouldNot be(deleteAuthRecord.refreshToken.value)
      }
    }

    "reject an invalid refresh_token request" should {
      "return 401 Unauthorised status if the client_id is unknown" in {
        status(accessToken(validRequest.copy(clientId = "unknown"))) shouldBe 401
      }

      "return 401 Unauthorised status if the client_secret is incorrect" in {
        status(accessToken(validRequest.copy(clientSecret = "incorrect"))) shouldBe 401
      }

      "return 400 Bad Request status if the refresh_token is incorrect" in {
        status(accessToken(validRequest.copy(refreshToken = "incorrect"))) shouldBe 400
      }
    }
  }

  private def accessToken(requestParams: RefreshTokenRequestParams, authRecords: AuthRecordOps = new DummyAuthRecords): Future[Result] = {
    val request = FakeRequest().withFormUrlEncodedBody(requestParams.toParams: _*)
    makeController(authRecords).accessToken(request)
  }

  private def makeController(authRecords: AuthRecordOps): OAuth2Controller = {
    val dh = new APIDataHandler(DummyClients, authRecords, DummyAuthCodes, DummyGatewayUsers, new SystemTimeSource)
    new OAuth2Controller(dh)
  }
}