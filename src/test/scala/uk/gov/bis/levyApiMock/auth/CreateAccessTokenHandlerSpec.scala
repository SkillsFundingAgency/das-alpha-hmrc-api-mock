package uk.gov.bis.levyApiMock.auth

import org.scalatest.{AsyncWordSpecLike, Matchers, OptionValues}
import uk.gov.bis.levyApiMock.data.oauth2.{AuthRecord, AuthRecordOps}
import uk.gov.bis.levyApiMock.data.stubs.{StubAuthRecordOps, StubClientOps}
import uk.gov.bis.levyApiMock.data.{Application, GatewayUser, TimeSource}

import scala.concurrent.{ExecutionContext, Future}
import scalaoauth2.provider.AuthInfo

class CreateAccessTokenHandlerSpec extends AsyncWordSpecLike with Matchers with OptionValues  {
  val nonPriviligedClientId = "non-privileged"
  val priviligedClientId = "privileged"

  "createAccessToken" should {
    "create an AuthToken for a non-privileged user" in {
      val authInfo = AuthInfo[GatewayUser](GatewayUser("", "", "", None, None), Some(nonPriviligedClientId), None, None)
      val mockAuthRecords = new MockAuthRecords
      handler(mockAuthRecords).createAccessToken(authInfo).map { _ =>
        mockAuthRecords.createdRecord.value.clientID shouldBe nonPriviligedClientId
        mockAuthRecords.createdRecord.value.privileged.value shouldBe false
      }
    }

    "create an AuthToken for a privileged user" in {
      val authInfo = AuthInfo[GatewayUser](GatewayUser("", "", "", None, None), Some(priviligedClientId), None, None)
      val mockAuthRecords = new MockAuthRecords
      handler(mockAuthRecords).createAccessToken(authInfo).map { _ =>
        mockAuthRecords.createdRecord.value.clientID shouldBe priviligedClientId
        mockAuthRecords.createdRecord.value.privileged.value shouldBe true
      }
    }
  }

  def handler(authRecordOps: AuthRecordOps) = new CreateAccessTokenHandler {
    override def authRecords = authRecordOps

    override def applications = DummyClients

    override def timeSource = new TimeSource {
      override def currentTimeMillis() = 100000L
    }

    override implicit def ec = scala.concurrent.ExecutionContext.Implicits.global
  }

  object DummyClients extends StubClientOps {
    val clients = Seq(
      Application("test", "appid1", nonPriviligedClientId, "clientsecret1", "servertoken1", privilegedAccess = false),
      Application("test", "appid2", priviligedClientId, "clientsecret2", "servertoken2", privilegedAccess = true)
    )

    override def forId(clientID: String)(implicit ec: ExecutionContext): Future[Option[Application]] = {
      Future.successful(clients.find(_.clientID == clientID))
    }
  }

  class MockAuthRecords extends StubAuthRecordOps {
    var createdRecord: Option[AuthRecord] = None

    override def create(record: AuthRecord)(implicit ec: ExecutionContext) = Future.successful {
      createdRecord = Some(record)
    }
  }

}
