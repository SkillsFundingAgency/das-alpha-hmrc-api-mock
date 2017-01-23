package uk.gov.bis.levyApiMock.controllers.security

import org.scalatest.concurrent.ScalaFutures
import org.scalatest.{Matchers, OptionValues, WordSpecLike}
import play.api.libs.json.{JsString, JsValue}
import play.api.test.FakeRequest
import play.api.test.Helpers._
import uk.gov.bis.levyApiMock.auth.APIDataHandler
import uk.gov.bis.levyApiMock.data.oauth2.{AuthRecord, AuthRecordOps}
import uk.gov.bis.levyApiMock.data.{Application, GatewayUser, MongoDate, SystemTimeSource}

import scala.concurrent.{ExecutionContext, Future}

class RefreshTokenSpec extends WordSpecLike with Matchers with ScalaFutures with OptionValues {
  implicit val ec = scala.concurrent.ExecutionContext.Implicits.global

  val user1 = "user1"
  val acesstoken1 = "acesstoken1"
  val clientid1 = "clientid1"
  val refreshtoken1 = "refreshtoken1"
  val clientsecret1 = "clientsecret1"

  "accessToken" can {
    "handle a valid refresh_token request" should {
      val mockAuthRecords = new MockAuthRecords
      val controller: OAuth2Controller = makeController(mockAuthRecords)
      val request = FakeRequest().withFormUrlEncodedBody(
        "grant_type" -> "refresh_token",
        "client_id" -> clientid1,
        "refresh_token" -> refreshtoken1,
        "client_secret" -> clientsecret1
      )

      val result = controller.accessToken(request)

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
        val request = FakeRequest().withFormUrlEncodedBody(
          "grant_type" -> "refresh_token",
          "client_id" -> "unknown",
          "refresh_token" -> refreshtoken1,
          "client_secret" -> clientsecret1
        )

        val controller = makeController()
        val result = controller.accessToken(request)

        status(result) shouldBe 401
      }

      "return 401 Unauthorised status if the client_secret is incorrect" in {
        val request = FakeRequest().withFormUrlEncodedBody(
          "grant_type" -> "refresh_token",
          "client_id" -> clientid1,
          "refresh_token" -> refreshtoken1,
          "client_secret" -> "incorrect"
        )

        val controller = makeController()
        val result = controller.accessToken(request)

        status(result) shouldBe 401
      }

      "return 400 Bad Request status if the refresh_token is incorrect" in {
        val request = FakeRequest().withFormUrlEncodedBody(
          "grant_type" -> "refresh_token",
          "client_id" -> clientid1,
          "refresh_token" -> "incorrect",
          "client_secret" -> clientsecret1
        )

        val controller = makeController()
        val result = controller.accessToken(request)

        status(result) shouldBe 400
      }
    }
  }

  private def makeController(authRecords: AuthRecordOps = new DummyAuthRecords) = {
    val dh = new APIDataHandler(DummyClients, authRecords, DummyAuthCodes, DummyGatewayUsers, new SystemTimeSource)
    new OAuth2Controller(dh)
  }

  object DummyClients extends StubClientOps {
    val clients = Seq(Application("test", "appid1", clientid1, clientsecret1, "servertoken1", privilegedAccess = false))

    override def forId(clientID: String)(implicit ec: ExecutionContext): Future[Option[Application]] = {
      Future.successful(clients.find(_.clientID == clientID))
    }
  }

  class DummyAuthRecords extends StubAuthRecordOps {
    val records = Seq(
      AuthRecord(acesstoken1, Some(refreshtoken1), user1, None, 3600, MongoDate.fromLong(0), clientid1, Some(false))
    )

    override def forRefreshToken(refreshToken: String)(implicit ec: ExecutionContext): Future[Option[AuthRecord]] = {
      Future.successful(records.find(_.refreshToken === Some(refreshToken)))
    }

    override def deleteExistingAndCreate(existing: AuthRecord, created: AuthRecord)(implicit ec: ExecutionContext): Future[Unit] = {
      Future.successful(())
    }
  }

  class MockAuthRecords extends DummyAuthRecords {
    var createdAuthRecord: Option[AuthRecord] = None
    var deletedAuthRecord: Option[AuthRecord] = None

    override def deleteExistingAndCreate(existing: AuthRecord, created: AuthRecord)(implicit ec: ExecutionContext): Future[Unit] = {
      deletedAuthRecord = Some(existing)
      createdAuthRecord = Some(created)
      Future.successful(())
    }
  }

  object DummyAuthCodes extends StubAuthCodeOps

  object DummyGatewayUsers extends StubGatewayUserOps {
    val users = Seq(GatewayUser(user1, "password", Some("empref1"), None, None))

    override def forGatewayID(gatewayID: String)(implicit ec: ExecutionContext): Future[Option[GatewayUser]] = {
      Future.successful(users.find(_.gatewayID === gatewayID))
    }
  }

}
