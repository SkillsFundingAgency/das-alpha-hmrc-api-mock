package uk.gov.bis.levyApiMock.auth

import java.util.Date

import uk.gov.bis.levyApiMock.data.oauth2.{AuthRecord, AuthRecordOps}
import uk.gov.bis.levyApiMock.data.{ClientOps, GatewayUser, TimeSource}

import scala.concurrent.{ExecutionContext, Future}
import scalaoauth2.provider.{AccessToken, AuthInfo}

trait CreateAccessTokenHandler {
  def timeSource: TimeSource

  def applications: ClientOps

  def authRecords: AuthRecordOps

  implicit def ec: ExecutionContext

  def createAccessToken(authInfo: AuthInfo[GatewayUser]): Future[AccessToken] = {
    for {
      privileged <- privilegedF(authInfo.clientId)
      auth = buildAuthRecord(authInfo, privileged = privileged)
      _ <- authRecords.create(auth)
    } yield buildAccessToken(auth)
  }

  private def privilegedF(clientId: Option[String]): Future[Boolean] = clientId.map { clientId =>
    applications.forId(clientId).map {
      case Some(app) => app.privilegedAccess
      case None => false
    }
  }.getOrElse(Future.successful(false))

  private val accessTokenExpiresInFourHours: Long = 4L * 60L * 60L

  private def buildAuthRecord(authInfo: AuthInfo[GatewayUser], privileged: Boolean): AuthRecord =
    AuthRecord(generateToken, Some(generateToken), None, authInfo.user.gatewayID, authInfo.scope, accessTokenExpiresInFourHours, timeSource.currentTimeMillis(), authInfo.clientId.get, Some(privileged))

  private def buildAccessToken(auth: AuthRecord) = {
    AccessToken(auth.accessToken, auth.refreshToken, auth.scope, Some(auth.expiresIn), new Date(auth.refreshedAt.map(_.longValue).getOrElse(timeSource.currentTimeMillis())))
  }
}
