package db.outh2

import java.sql.Date
import javax.inject.Inject

import db.DBModule
import play.api.db.slick.DatabaseConfigProvider

import scala.concurrent.{ExecutionContext, Future}

case class AccessTokenRow(
                           accessToken: String,
                           scope: String,
                           expiresAt: Date,
                           createdAt: Date
                         )

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
      _ <- AccessTokens.filter(a => a.accessToken === token.accessToken).delete
      a <- AccessTokens += token
    } yield a.result
  }

  class AccessTokenTable(tag: Tag) extends Table[AccessTokenRow](tag, "ACCESS_TOKEN") {
    def accessToken = column[String]("ACCESS_TOKEN", O.PrimaryKey)

    def scope = column[String]("SCOPE")

    def expiresAt = column[Date]("EXPIRES_AT")

    def createdAt = column[Date]("CREATED_AT")

    def * = (accessToken, scope, expiresAt, createdAt) <>(AccessTokenRow.tupled, AccessTokenRow.unapply)

  }

}

class AccessTokenDAO @Inject()(protected val dbConfigProvider: DatabaseConfigProvider)(implicit val ec: ExecutionContext) extends AccessTokenModule