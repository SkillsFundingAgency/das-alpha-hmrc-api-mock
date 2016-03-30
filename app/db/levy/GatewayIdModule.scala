package db.levy

import javax.inject.Inject

import db.DBModule
import play.api.db.slick.DatabaseConfigProvider

import scala.concurrent.{ExecutionContext, Future}

case class GatewayIdSchemeRow(id: String, empref: String)

trait GatewayIdModule extends DBModule {
  self: SchemeModule =>

  import driver.api._

  val GatewayIds = TableQuery[GatewayIdSchemeTable]

  class GatewayIdSchemeTable(tag: Tag) extends Table[GatewayIdSchemeRow](tag, "gateway_id_scheme") {
    def id = column[String]("id")

    def empref = column[String]("empref")

    def emprefFk = foreignKey("gateway_id_scheme_fk", empref, Schemes)(_.empref, onDelete = ForeignKeyAction.Cascade)

    def pk = primaryKey("gateway_id_scheme_pk", (id, empref))

    def * = (id, empref) <>(GatewayIdSchemeRow.tupled, GatewayIdSchemeRow.unapply)
  }

}

class GatewayIdDAO @Inject()(protected val dbConfigProvider: DatabaseConfigProvider)(implicit val ec: ExecutionContext) extends GatewayIdModule with SchemeModule {

  import driver.api._

  def emprefsForId(gatewayId: String): Future[Seq[String]] = db.run {
    GatewayIds.filter(_.id === gatewayId).map(_.empref).result
  }

  def bindEmprefs(gatewayId: String, emprefs: List[String]): Future[Unit] =
    emprefsForId(gatewayId).flatMap { existingEmprefs =>
      val toInsert = emprefs.filter(e => !existingEmprefs.contains(e)).map(e => GatewayIdSchemeRow(gatewayId, e))
      db.run(GatewayIds ++= toInsert)
    }.map(_ => ())

}
