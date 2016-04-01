package db.outh2

import javax.inject.{Inject, Singleton}

import db.DBModule
import org.joda.time.format._
import play.api.db.slick.DatabaseConfigProvider
import slick.dbio.Effect.Write

import scala.concurrent.{ExecutionContext, Future}

case class AccessTokenRow(
                           accessToken: String,
                           scope: String,
                           gatewayId: String,
                           clientId: String,
                           expiresAt: Long,
                           createdAt: Long
                         ) {
  val expiresAtDateString: String = DateTimeFormat.forPattern("dd/MM/yyyy HH:mm:ss ZZ").print(expiresAt)
}

trait AccessTokenModule extends DBModule {

  import driver.api._

  val AccessTokens = TableQuery[AccessTokenTable]

  class AccessTokenTable(tag: Tag) extends Table[AccessTokenRow](tag, "access_token") {
    def clientId = column[String]("client_id")

    def scope = column[String]("scope")

    def gatewayId = column[String]("gateway_id")

    def pk = primaryKey("access_token_pk", (clientId, scope))

    def accessToken = column[String]("access_token")

    def expiresAt = column[Long]("expires_at")

    def createdAt = column[Long]("created_at")

    def * = (accessToken, scope, gatewayId, clientId, expiresAt, createdAt) <>(AccessTokenRow.tupled, AccessTokenRow.unapply)
  }

}

@Singleton
class AccessTokenDAO @Inject()(protected val dbConfigProvider: DatabaseConfigProvider)(implicit val ec: ExecutionContext) extends AccessTokenModule {

  import driver.api._

  def all(): Future[Seq[AccessTokenRow]] = run(AccessTokens.result)

  def find(accessToken: String): Future[Option[AccessTokenRow]] = run(AccessTokens.filter(_.accessToken === accessToken).result.headOption)

  val deleteExpired: DBIOAction[Int, NoStream, Write] = AccessTokens.filter(_.expiresAt < System.currentTimeMillis()).delete

  def cleanup(): Future[Unit] = run(deleteExpired).map(_ => ())

  def create(token: AccessTokenRow): Future[Unit] = run(AccessTokens += token).map(_ => ())

  def clearExpired(): Future[Unit] = run(deleteExpired).map(_ => ())

  def deleteExistingAndCreate(token: AccessTokenRow): Future[Unit] = run {
    for {
      _ <- AccessTokens.filter(a => a.scope === token.scope).delete
      a <- AccessTokens += token
    } yield a.result
  }
}