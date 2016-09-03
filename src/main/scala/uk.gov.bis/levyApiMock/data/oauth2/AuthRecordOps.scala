package uk.gov.bis.levyApiMock.data.oauth2

import org.joda.time.format.DateTimeFormat
import uk.gov.bis.levyApiMock.data.MongoDate

import scala.concurrent.{ExecutionContext, Future}

case class AuthRecord(
                       accessToken: String,
                       refreshToken: Option[String],
                       gatewayID: String,
                       scope: Option[String],
                       expiresIn: Long,
                       createdAt: MongoDate,
                       clientID: String) {
  val expiresAt: Long = createdAt.longValue + expiresIn
  val expiresAtDateString: String = DateTimeFormat.forPattern("dd/MM/yyyy HH:mm:ss ZZ").print(expiresAt)
}

trait AuthRecordOps {
  def forRefreshToken(refreshToken: String)(implicit ec: ExecutionContext): Future[Option[AuthRecord]]

  def forAccessToken(accessToken: String)(implicit ec: ExecutionContext): Future[Option[AuthRecord]]

  def find(accessToken: String)(implicit ec: ExecutionContext): Future[Option[AuthRecord]]

  def find(gatewayId: String, clientId: Option[String])(implicit ec: ExecutionContext): Future[Option[AuthRecord]]

  def create(record: AuthRecord)(implicit ec: ExecutionContext): Future[Unit]

  def deleteExistingAndCreate(token: AuthRecord)(implicit ec: ExecutionContext): Future[Unit]
}
