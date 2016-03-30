package db.outh2

import javax.inject.{Inject, Singleton}

import db.DBModule
import org.joda.time._
import org.joda.time.format._
import play.api.db.slick.DatabaseConfigProvider

import scala.concurrent.{ExecutionContext, Future}

case class AccessTokenRow(
                           accessToken: String,
                           scope: String,
                           clientId: String,
                           expiresAt: Long,
                           createdAt: Long
                         ) {
  val expiresAtDateString: String = DateTimeFormat.forPattern("dd/MM/yyyy HH:mm:ss ZZ").print(expiresAt)
}

trait AccessTokenModule extends DBModule {

  import driver.api._

  implicit def ec: ExecutionContext

  val AccessTokens = TableQuery[AccessTokenTable]

  def all(): Future[Seq[AccessTokenRow]] = db.run(AccessTokens.result)

  def find(accessToken: String): Future[Option[AccessTokenRow]] = db.run {
    AccessTokens.filter(_.accessToken === accessToken).result.headOption
  }

  def deleteExistingAndCreate(token: AccessTokenRow): Future[Unit] = db.run {
    for {
      _ <- AccessTokens.filter(a => a.scope === token.scope).delete
      a <- AccessTokens += token
    } yield a.result
  }

  class AccessTokenTable(tag: Tag) extends Table[AccessTokenRow](tag, "access_token") {
    def clientId = column[String]("client_id")

    def scope = column[String]("scope")

    def pk = primaryKey("access_token_pk", (clientId, scope))

    def accessToken = column[String]("access_token")

    def expiresAt = column[Long]("expires_at")

    def createdAt = column[Long]("created_at")

    def * = (accessToken, scope, clientId, expiresAt, createdAt) <>(AccessTokenRow.tupled, AccessTokenRow.unapply)

  }

}

@Singleton
class AccessTokenDAO @Inject()(protected val dbConfigProvider: DatabaseConfigProvider)(implicit val ec: ExecutionContext) extends AccessTokenModule