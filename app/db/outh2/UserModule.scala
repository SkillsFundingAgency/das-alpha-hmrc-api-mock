package db.outh2

import javax.inject.Inject

import db.DBModule
import play.api.db.slick.DatabaseConfigProvider

import scala.concurrent.Future

case class UserRow(id: Long, name: String, hashedPassword: String)

trait UserModule extends DBModule {

  import driver.api._

  val Users = TableQuery[UserTable]

  def all(): Future[Seq[UserRow]] = db.run(Users.result)

  def byName(s: String): Future[Option[UserRow]] = db.run(Users.filter(u => u.name === s).result.headOption)

  class UserTable(tag: Tag) extends Table[UserRow](tag, "USER") {

    def id = column[Long]("ID", O.PrimaryKey)

    def name = column[String]("NAME")

    def password = column[String]("PASSWORD")

    def * = (id, name, password) <>(UserRow.tupled, UserRow.unapply)

  }

}

class UserDAO @Inject()(protected val dbConfigProvider: DatabaseConfigProvider) extends UserModule