package db.client

import javax.inject.{Inject, Singleton}

import db.DBModule
import play.api.db.slick.DatabaseConfigProvider

import scala.concurrent.{ExecutionContext, Future}

case class SchemeRow(empref: String, utr: Option[String])


trait SchemeModule extends DBModule {
  self: OrganisationModule =>

  import driver.api._

  val Schemes = TableQuery[SchemeTable]

  def insert(cat: SchemeRow): Future[Unit] = db.run(Schemes += cat).map { _ => () }

  def byUtr(utr: String): Future[Seq[SchemeRow]] = db.run(Schemes.filter(_.utr === utr).result)

  class SchemeTable(tag: Tag) extends Table[SchemeRow](tag, "SCHEME") {

    def empref = column[String]("EMPREF", O.PrimaryKey)

    def utr = column[Option[String]]("UTR")

    def organisationFK = foreignKey("scheme_org_fk", utr, Organisations)(_.utr.?, onDelete = ForeignKeyAction.Cascade)

    def * = (empref, utr) <>(SchemeRow.tupled, SchemeRow.unapply)
  }

}

@Singleton
class SchemeDAO @Inject()(protected val dbConfigProvider: DatabaseConfigProvider)(implicit val ec: ExecutionContext) extends SchemeModule with OrganisationModule