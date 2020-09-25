package uk.gov.bis.levyApiMock.data.oauth2

import org.joda.time.{DateTime}

import scala.concurrent.{ExecutionContext, Future}

case class AuthRecord(
                       accessToken: String,
                       refreshToken: Option[String],
                       refreshedAt: Option[DateTime],
                       gatewayID: String,
                       scope: Option[String],
                       expiresIn: Long,
                       createdAt: DateTime,
                       clientID: String,
                       privileged: Option[Boolean]) {
  val eighteenMonths: Long = 18 * 30 * 24 * 60 * 60 * 1000L

  val accessTokenExpiresAt: Long = refreshedAt.getOrElse(createdAt).getMillis() + expiresIn * 1000L
  val refreshTokenExpiresAt: Long = createdAt.getMillis() + eighteenMonths
  val isPrivileged: Boolean = privileged.getOrElse(false)

  def accessTokenExpired(referenceTimeInMills: Long): Boolean = accessTokenExpiresAt <= referenceTimeInMills

  def refreshTokenExpired(referenceTimeInMills: Long): Boolean = refreshTokenExpiresAt <= referenceTimeInMills
}

trait AuthRecordOps {
  def forRefreshToken(refreshToken: String)(implicit ec: ExecutionContext): Future[Option[AuthRecord]]

  def forAccessToken(accessToken: String)(implicit ec: ExecutionContext): Future[Option[AuthRecord]]

  def find(accessToken: String)(implicit ec: ExecutionContext): Future[Option[AuthRecord]]

  def find(gatewayId: String, clientId: Option[String])(implicit ec: ExecutionContext): Future[Option[AuthRecord]]

  def create(record: AuthRecord)(implicit ec: ExecutionContext): Future[Unit]

  def deleteExistingAndCreate(existing: AuthRecord, created: AuthRecord)(implicit ec: ExecutionContext): Future[Unit]
}
