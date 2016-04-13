package db.oauth2

import javax.inject.Inject

import data.oauth2.{AuthRecord, AuthRecordOps}
import db.SlickModule
import db.levy.GatewayIdSchemes
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

class AuthRecords @Inject()(protected val dbConfigProvider: DatabaseConfigProvider)(implicit val ec: ExecutionContext) extends AuthRecordModule

class AuthRecordDAO @Inject()(protected val authRecords: AuthRecords, gatewayIdSchemes: GatewayIdSchemes)
  extends AuthRecordOps {

  import authRecords._
  import authRecords.api._
  import gatewayIdSchemes.GatewayIdSchemes

  def all(): Future[Seq[AuthRecord]] = run(AccessTokens.result)

  val expiredTokens = AccessTokens.filter(_.expiresAt < System.currentTimeMillis())

  val activeTokens = AccessTokens.filter(_.expiresAt >= System.currentTimeMillis())

  /**
    * Find a row with the given access token that has not expired
    */
  def find(accessToken: String): Future[Option[AuthRecord]] = run(activeTokens.filter(t => t.accessToken === accessToken).result.headOption)

  /**
    * Find an AuthRecordRow matching the token and scope, and which allows access to a gateway id
    * that has the taxId enrolled.
    */
  def find(accessToken: String, taxId: String, scope: String): Future[Option[AuthRecord]] = run {
    val q = for {
      t <- activeTokens if t.accessToken === accessToken && t.scope === scope
      i <- GatewayIdSchemes if i.id === t.gatewayId && i.empref === taxId
    } yield t

    q.result.headOption
  }

  def clearExpired(): Future[Unit] = run(expiredTokens.delete).map(_ => ())

  def create(token: AuthRecord): Future[Unit] = run(AccessTokens += token).map(_ => ())

  override def expire(token: String): Future[Int] = run {
    val q = for {
      t <- AccessTokens if t.accessToken === token
    } yield t.expiresAt

    q.update(System.currentTimeMillis())
  }
}