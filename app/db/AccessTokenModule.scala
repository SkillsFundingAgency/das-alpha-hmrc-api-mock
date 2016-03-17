package db

import java.sql.Date
import javax.inject.Inject

import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.dbio.Effect.Read
import slick.driver.JdbcProfile

import scala.concurrent.{ExecutionContext, Future}

case class AccessTokenRow(
                           accessToken: String,
                           refreshToken: Option[String],
                           userId: Long, scope: Option[String],
                           expiresIn: Option[Long],
                           createdAt: Date,
                           clientId: Option[String])

trait AccessTokenModule extends HasDatabaseConfigProvider[JdbcProfile] {

  import driver.api._

  implicit def ec: ExecutionContext

  val AccessTokens = TableQuery[AccessTokenTable]

  def all(): Future[Seq[AccessTokenRow]] = db.run(AccessTokens.result)

  def deleteExistingAndCreate(token: AccessTokenRow): Future[Unit] = db.run {
    for {
      _ <- AccessTokens.filter(a => a.clientId === token.clientId && a.userId === token.userId).delete
      a <- AccessTokens += token
    } yield a.result
  }

  class AccessTokenTable(tag: Tag) extends Table[AccessTokenRow](tag, "AccessToken") {
    def accessToken = column[String]("access_token", O.PrimaryKey)

    def refreshToken = column[Option[String]]("refresh_token")

    def userId = column[Long]("user_id")

    def scope = column[Option[String]]("scope")

    def expiresIn = column[Option[Long]]("expires_in")

    def createdAt = column[Date]("created_at")

    def clientId = column[Option[String]]("client_id")

    def * = (accessToken, refreshToken, userId, scope, expiresIn, createdAt, clientId) <>(AccessTokenRow.tupled, AccessTokenRow.unapply)

  }

}

class AccessTokenDAO @Inject()(protected val dbConfigProvider: DatabaseConfigProvider)(implicit val ec: ExecutionContext) extends AccessTokenModule