package db.oauth2

import javax.inject.Inject

import data.oauth2.{AuthRecord, AuthRecordOps}
import db.SlickModule
import db.levy.Enrolments
import play.api.db.slick.DatabaseConfigProvider

import scala.concurrent.{ExecutionContext, Future}

trait AuthRecordModule extends SlickModule {

  import driver.api._

  val AccessTokens = TableQuery[AccessTokenTable]

  class AccessTokenTable(tag: Tag) extends Table[AuthRecord](tag, "auth_record") {
    def clientId = column[String]("client_id")

    def gatewayId = column[String]("gateway_id")

    def scope = column[String]("scope")

    def accessToken = column[String]("access_token")

    def expiresAt = column[Long]("expires_at")

    def createdAt = column[Long]("created_at")

    def * = (accessToken, scope, gatewayId, clientId, expiresAt, createdAt) <>(AuthRecord.tupled, AuthRecord.unapply)
  }

}

class AuthRecords @Inject()(protected val dbConfigProvider: DatabaseConfigProvider) extends AuthRecordModule

class AuthRecordDAO @Inject()(protected val authRecords: AuthRecords, gatewayIdSchemes: Enrolments)
  extends AuthRecordOps {

  import authRecords._
  import authRecords.api._
  import gatewayIdSchemes.Enrolments

  override def all()(implicit ec: ExecutionContext): Future[Seq[AuthRecord]] = run(AccessTokens.result)

  val expiredTokens = AccessTokens.filter(_.expiresAt < System.currentTimeMillis())

  val activeTokens = AccessTokens.filter(_.expiresAt >= System.currentTimeMillis())

  /**
    * Find a row with the given access token that has not expired
    */
  override def find(accessToken: String)(implicit ec: ExecutionContext): Future[Option[AuthRecord]] = run(activeTokens.filter(t => t.accessToken === accessToken).result.headOption)

  /**
    * Find an AuthRecordRow matching the token and scope, and which allows access to a gateway id
    * that has the taxId enrolled.
    */
  override def find(accessToken: String, identifierType: String, taxId: String, scope: String)(implicit ec: ExecutionContext): Future[Option[AuthRecord]] = run {
    val q = for {
      t <- activeTokens if t.accessToken === accessToken && t.scope === scope
      i <- Enrolments if i.gatewayId === t.gatewayId && i.taxId === taxId
    } yield t

    q.result.headOption
  }

  override def clearExpired()(implicit ec: ExecutionContext): Future[Unit] = run(expiredTokens.delete).map(_ => ())

  override def create(token: AuthRecord)(implicit ec: ExecutionContext): Future[Unit] = run(AccessTokens += token).map(_ => ())

  override def expire(token: String)(implicit ec: ExecutionContext): Future[Int] = run {
    val q = for {
      t <- AccessTokens if t.accessToken === token
    } yield t.expiresAt

    q.update(System.currentTimeMillis())
  }
}