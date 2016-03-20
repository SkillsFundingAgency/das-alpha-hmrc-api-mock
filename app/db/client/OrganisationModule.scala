package db.client

import javax.inject.{Inject, Singleton}

import db.DBModule
import play.api.db.slick.DatabaseConfigProvider

import scala.concurrent.{ExecutionContext, Future}

case class OrganisationRow(utr: String, name: String)


trait OrganisationModule extends DBModule {

  import driver.api._

  val Organisations = TableQuery[OrganisationTable]

  def insert(cat: OrganisationRow): Future[Unit] = db.run(Organisations += cat).map { _ => () }

  class OrganisationTable(tag: Tag) extends Table[OrganisationRow](tag, "ORGANISATION") {

    def utr = column[String]("UTR", O.PrimaryKey)

    def name = column[String]("NAME")

    def * = (utr, name) <>(OrganisationRow.tupled, OrganisationRow.unapply)
  }

}

@Singleton
class OrganisationDAO @Inject()(protected val dbConfigProvider: DatabaseConfigProvider)(implicit val ec: ExecutionContext) extends OrganisationModule