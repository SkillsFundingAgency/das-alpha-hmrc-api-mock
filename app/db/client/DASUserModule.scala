package db.client

import javax.inject.Inject

import db.DBModule
import play.api.db.slick.DatabaseConfigProvider

import scala.concurrent.{ExecutionContext, Future}

case class UserRow(id: Long, name: String, hashedPassword: String)

trait DASUserModule extends DBModule {

  import driver.api._

  val DASUsers = TableQuery[UserTable]

  def all(): Future[Seq[UserRow]] = db.run(DASUsers.result)

  def validate(username:String, password:String):Future[Option[UserRow]] = db.run{
    DASUsers.filter(u => u.name === username && u.password === password).result.headOption
  }

  def byId(id: Long): Future[Option[UserRow]] = db.run(DASUsers.filter(_.id === id).result.headOption)

  def byName(s: String): Future[Option[UserRow]] = db.run(DASUsers.filter(u => u.name === s).result.headOption)

  class UserTable(tag: Tag) extends Table[UserRow](tag, "DAS_USER") {

    def id = column[Long]("ID", O.PrimaryKey)

    def name = column[String]("NAME")

    def password = column[String]("PASSWORD")

    def * = (id, name, password) <>(UserRow.tupled, UserRow.unapply)

  }

}

class DASUserDAO @Inject()(protected val dbConfigProvider: DatabaseConfigProvider)(implicit val ec: ExecutionContext) extends DASUserModule