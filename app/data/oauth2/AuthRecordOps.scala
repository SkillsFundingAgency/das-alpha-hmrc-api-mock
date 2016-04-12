package data.oauth2

import org.joda.time.format.DateTimeFormat

import scala.concurrent.Future

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
  def all(): Future[Seq[AuthRecord]]

  def find(accessToken: String): Future[Option[AuthRecord]]

  def find(accessToken: String, taxId: String, scope: String): Future[Option[AuthRecord]]

  def clearExpired(): Future[Unit]

  def create(token: AuthRecord): Future[Unit]

  def expire(token: String): Future[Int]
}
