package uk.gov.bis.levyApiMock.data.oauth2

import org.joda.time.format.DateTimeFormat

import scala.concurrent.{ExecutionContext, Future}

case class AuthRecord(
                       accessToken: String,
                       gatewayID: String,
                       clientID: String,
                       expiresIn: Long,
                       createdAt: Long
                     ) {
  val expiresAt = createdAt+expiresIn
  val expiresAtDateString: String = DateTimeFormat.forPattern("dd/MM/yyyy HH:mm:ss ZZ").print(expiresAt)
}

trait AuthRecordOps {
  def find(accessToken: String)(implicit ec: ExecutionContext): Future[Option[AuthRecord]]
}
