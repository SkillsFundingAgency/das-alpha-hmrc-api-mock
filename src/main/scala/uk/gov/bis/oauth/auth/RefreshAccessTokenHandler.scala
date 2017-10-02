package uk.gov.bis.oauth.auth

import java.util.Date

import play.api.Logger
import uk.gov.bis.oauth.data.{AuthRecordOps, GatewayUser}
import uk.gov.bis.utils.TimeSource

import scala.concurrent.{ExecutionContext, Future}
import scalaoauth2.provider.{AccessToken, AuthInfo}

trait RefreshAccessTokenHandler {
  def timeSource: TimeSource

  def authRecords: AuthRecordOps

  implicit def ec: ExecutionContext

  def refreshAccessToken(authInfo: AuthInfo[GatewayUser], refreshToken: String): Future[AccessToken] = {
    OAuthTrace("refresh access token")

    authRecords.forRefreshToken(refreshToken).flatMap {
      case Some(authRecord) if !authRecord.refreshTokenExpired(timeSource.currentTimeMillis()) =>
        val refreshedAt = timeSource.currentTimeMillis()
        val expireInOneHour = Some(60L * 60L)
        val updatedRow = authRecord.copy(accessToken = generateToken, refreshToken = Some(generateToken), refreshedAt = Some(refreshedAt))
        for {
          _ <- authRecords.deleteExistingAndCreate(authRecord, updatedRow)
        } yield AccessToken(updatedRow.accessToken, updatedRow.refreshToken, authInfo.scope, expireInOneHour, new Date(refreshedAt))

      case Some(authRecord) =>
        val s = s"Refresh token has expired"
        Logger.warn(s"$s $authRecord")
        throw new IllegalArgumentException(s)

      case None =>
        val s = s"Cannot find an access token entry with refresh token $refreshToken"
        Logger.warn(s)
        throw new IllegalArgumentException(s)
    }
  }
}
