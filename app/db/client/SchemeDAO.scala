package db.client

import javax.inject.Inject

import db.DBModule
import play.api.db.slick.DatabaseConfigProvider

import scala.concurrent.{ExecutionContext, Future}

case class SchemeRow(empref: String, utr: Option[String])

class SchemeDAO @Inject()(protected val dbConfigProvider: DatabaseConfigProvider, val organisationDAO: OrganisationDAO)(implicit val ec: ExecutionContext) extends DBModule {

  import driver.api._

  val Schemes = TableQuery[SchemeTable]

  def all(): Future[Seq[SchemeRow]] = db.run(Schemes.result)

  def insert(cat: SchemeRow): Future[Unit] = db.run(Schemes += cat).map { _ => () }

  def byUtr(utr: String): Future[Seq[SchemeRow]] = db.run(Schemes.filter(_.utr === utr).result)

  class SchemeTable(tag: Tag) extends Table[SchemeRow](tag, "SCHEME") {

    def empref = column[String]("EMPREF", O.PrimaryKey)

    def utr = column[Option[String]]("UTR")

    def organisationFK = foreignKey("scheme_org_fk", utr, organisationDAO.Organisations)(_.utr.?, onDelete = ForeignKeyAction.Cascade)

    def * = (empref, utr) <>(SchemeRow.tupled, SchemeRow.unapply)
  }
}