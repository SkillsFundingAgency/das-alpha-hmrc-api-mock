package uk.gov.bis.oauth.auth

import org.scalatest.{AsyncWordSpecLike, Matchers, OptionValues}
import uk.gov.bis.levyApiMock.data.CommonTestData._
import uk.gov.bis.levyApiMock.data.stubs.StubAuthRecordOps
import uk.gov.bis.oauth.data.{AuthRecord, AuthRecordOps, GatewayUser}
import uk.gov.bis.utils.TimeSource

import scala.concurrent.{ExecutionContext, Future}
import scalaoauth2.provider.AuthInfo

class CreateAccessTokenHandlerSpec extends AsyncWordSpecLike with Matchers with OptionValues  {

  "createAccessToken" should {
    "create an AuthToken for a non-privileged user" in {
      val authInfo = AuthInfo[GatewayUser](GatewayUser("", "", None, None, None), Some(nonPriviligedClientId), None, None)
      val mockAuthRecords = new MockAuthRecords
      handler(mockAuthRecords).createAccessToken(authInfo).map { _ =>
        mockAuthRecords.createdRecord.value.clientID shouldBe nonPriviligedClientId
        mockAuthRecords.createdRecord.value.privileged.value shouldBe false
      }
    }

    "create an AuthToken for a privileged user" in {
      val authInfo = AuthInfo[GatewayUser](GatewayUser("", "", None, None, None), Some(priviligedClientId), None, None)
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

  class MockAuthRecords extends StubAuthRecordOps {
    var createdRecord: Option[AuthRecord] = None

    override def create(record: AuthRecord)(implicit ec: ExecutionContext) = Future.successful {
      createdRecord = Some(record)
    }
  }

}
