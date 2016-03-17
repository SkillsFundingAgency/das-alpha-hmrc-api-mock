package db

import javax.inject.Inject

import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.driver.JdbcProfile

import scala.concurrent.{ExecutionContext, Future}

case class ClientRow(id: String, secret: Option[String], redirectUri: Option[String], scope: Option[String])

class ClientDAO @Inject()(protected val dbConfigProvider: DatabaseConfigProvider)(implicit ec: ExecutionContext) extends HasDatabaseConfigProvider[JdbcProfile] {

  import driver.api._

  val Clients = TableQuery[ClientTable]

  def validate(id: String, secret: Option[String], grantType: String): Future[Boolean] = db.run {
    Clients.filter(c => c.id === id).result.headOption.map(_.isDefined)
  }

  class ClientTable(tag: Tag) extends Table[ClientRow](tag, "CLIENT") {
    def id = column[String]("id", O.PrimaryKey)

    def secret = column[Option[String]]("secret")

    def redirectUri = column[Option[String]]("redirect_uri")

    def scope = column[Option[String]]("scope")

    def grantType = column[String]("grant_type")

    def * = (id, secret, redirectUri, scope) <>(ClientRow.tupled, ClientRow.unapply)
  }

}