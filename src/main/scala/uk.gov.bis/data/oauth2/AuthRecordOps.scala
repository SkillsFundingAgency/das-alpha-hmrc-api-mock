package uk.gov.bis.data.oauth2

import org.joda.time.format.DateTimeFormat

import scala.concurrent.{ExecutionContext, Future}

case class AuthRecord(
                       accessToken: String,
                       scope: String,
                       gatewayId: String,
                       clientId: String,
                       expiresAt: Long,
                       createdAt: Long
                     ) {
  val expiresAtDateString: String = DateTimeFormat.forPattern("dd/MM/yyyy HH:mm:ss ZZ").print(expiresAt)
}

trait AuthRecordOps {
  def all()(implicit ec: ExecutionContext): Future[Seq[AuthRecord]]

  def find(accessToken: String)(implicit ec: ExecutionContext): Future[Option[AuthRecord]]

  def find(accessToken: String, identifierType: String, taxId: String, scope: String)(implicit ec: ExecutionContext): Future[Option[AuthRecord]]

  def clearExpired()(implicit ec: ExecutionContext): Future[Unit]

  def create(record: AuthRecord)(implicit ec: ExecutionContext): Future[Unit]

  def create(records: Seq[AuthRecord])(implicit ec: ExecutionContext): Future[Unit]

  def expire(token: String)(implicit ec: ExecutionContext): Future[Int]

  def scopes(token:String)(implicit ec: ExecutionContext): Future[Seq[String]]
}
