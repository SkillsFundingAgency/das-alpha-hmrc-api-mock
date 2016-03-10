package db


import javax.inject.Inject

import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import slick.driver.JdbcProfile

import scala.concurrent.Future

case class SchemeRow(empref: String, utr: Option[String])

class SchemeDAO @Inject()(protected val dbConfigProvider: DatabaseConfigProvider) extends HasDatabaseConfigProvider[JdbcProfile] {

  import driver.api._

  private val Schemes = TableQuery[SchemeTable]

  def all(): Future[Seq[SchemeRow]] = db.run(Schemes.result)

  def insert(cat: SchemeRow): Future[Unit] = db.run(Schemes += cat).map { _ => () }

  private class SchemeTable(tag: Tag) extends Table[SchemeRow](tag, "SCHEME") {

    def empref = column[String]("EMPREF", O.PrimaryKey)

    def utr = column[Option[String]]("UTR")

    def * = (empref, utr) <>(SchemeRow.tupled, SchemeRow.unapply)
  }

}