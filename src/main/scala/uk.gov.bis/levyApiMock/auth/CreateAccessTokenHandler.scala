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
    OAuthTrace(s"create access token for $authInfo")
    // 1 hour
    val refreshToken = Some(generateToken)
    val accessTokenExpiresIn = 60L * 60L
    val accessToken = generateToken
    val createdAt = timeSource.currentTimeMillis()

    def buildAuthRecord(privileged: Boolean): AuthRecord = {
      AuthRecord(accessToken, refreshToken, None, authInfo.user.gatewayID, authInfo.scope, accessTokenExpiresIn, createdAt, authInfo.clientId.get, Some(privileged))
    }

    val privilegedF = authInfo.clientId.map { clientId =>
      applications.forId(clientId).map {
        case Some(app) => app.privilegedAccess
        case None => false
      }
    }.getOrElse(Future.successful(false))

    for {
      privileged <- privilegedF
      auth = buildAuthRecord(privileged = privileged)
      _ <- authRecords.create(auth)
    } yield AccessToken(auth.accessToken, auth.refreshToken, auth.scope, Some(auth.expiresIn), new Date(auth.refreshedAt.map(_.longValue).getOrElse(createdAt)))
  }
}
