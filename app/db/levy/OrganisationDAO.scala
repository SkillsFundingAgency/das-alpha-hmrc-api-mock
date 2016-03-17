package db.levy

import javax.inject.Inject

import db.DBModule
import play.api.db.slick.DatabaseConfigProvider

import scala.concurrent.{ExecutionContext, Future}

case class OrganisationRow(utr: String, name: String)

class OrganisationDAO @Inject()(protected val dbConfigProvider: DatabaseConfigProvider)(implicit val ec: ExecutionContext) extends DBModule {

  import driver.api._

  val Organisations = TableQuery[OrganisationTable]

  def all(): Future[Seq[OrganisationRow]] = db.run(Organisations.result)

  def insert(cat: OrganisationRow): Future[Unit] = db.run(Organisations += cat).map { _ => () }

  class OrganisationTable(tag: Tag) extends Table[OrganisationRow](tag, "ORGANISATION") {

    def utr = column[String]("UTR", O.PrimaryKey)

    def name = column[String]("NAME")

    def * = (utr, name) <>(OrganisationRow.tupled, OrganisationRow.unapply)
  }

}