package uk.gov.bis.levyApiMock.controllers.security

import uk.gov.bis.levyApiMock.data._
import uk.gov.bis.levyApiMock.data.oauth2.AuthRecord
import uk.gov.bis.levyApiMock.data.stubs.{StubAuthCodeOps, StubAuthRecordOps, StubClientOps, StubGatewayUserOps}

import scala.concurrent.{ExecutionContext, Future}

object RefreshTokenSpecTestData {
  val user1 = "user1"
  val acesstoken1 = "acesstoken1"
  val clientid1 = "clientid1"
  val refreshtoken1 = "refreshtoken1"
  val clientsecret1 = "clientsecret1"

  case class RefreshTokenRequestParams(clientId: String, clientSecret: String, refreshToken: String) {
    def toParams: Seq[(String, String)] = Seq(
      "grant_type" -> "refresh_token",
      "client_id" -> clientId,
      "refresh_token" -> refreshToken,
      "client_secret" -> clientSecret
    )
  }

  val validRequest = RefreshTokenRequestParams(clientid1, clientsecret1, refreshtoken1)

  object DummyClients extends StubClientOps {
    val clients = Seq(Application("test", "appid1", clientid1, clientsecret1, "servertoken1", privilegedAccess = false))

    override def forId(clientID: String)(implicit ec: ExecutionContext): Future[Option[Application]] = {
      Future.successful(clients.find(_.clientID == clientID))
    }
  }

  class DummyAuthRecords extends StubAuthRecordOps {
    val records = Seq(
      AuthRecord(acesstoken1, Some(refreshtoken1), Some(MongoDate.fromLong(System.currentTimeMillis())), user1, None, 3600, MongoDate.fromLong(System.currentTimeMillis()), clientid1, Some(false))
    )

    override def forRefreshToken(refreshToken: String)(implicit ec: ExecutionContext): Future[Option[AuthRecord]] = {
      Future.successful(records.find(_.refreshToken.contains(refreshToken)))
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
    val users = Seq(GatewayUser(user1, "password", "empref1", None, None))

    override def forGatewayID(gatewayID: String)(implicit ec: ExecutionContext): Future[Option[GatewayUser]] = {
      Future.successful(users.find(_.gatewayID == gatewayID))
    }
  }

}
