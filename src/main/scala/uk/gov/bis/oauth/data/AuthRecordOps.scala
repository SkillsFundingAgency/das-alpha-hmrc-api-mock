package uk.gov.bis.oauth.data

import uk.gov.bis.mongo.MongoDate

import scala.concurrent.{ExecutionContext, Future}

case class AuthRecord(
                       accessToken: String,
                       refreshToken: Option[String],
                       refreshedAt: Option[MongoDate],
                       gatewayID: String,
                       scope: Option[String],
                       expiresIn: Long,
                       createdAt: MongoDate,
                       clientID: String,
                       privileged: Option[Boolean]) {
  val eighteenMonths: Long = 18 * 30 * 24 * 60 * 60 * 1000L

  val accessTokenExpiresAt: Long = refreshedAt.getOrElse(createdAt).longValue + expiresIn * 1000L
  val refreshTokenExpiresAt: Long = createdAt.longValue + eighteenMonths
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
