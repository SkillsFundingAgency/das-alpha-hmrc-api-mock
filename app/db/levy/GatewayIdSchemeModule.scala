package db.levy

import javax.inject.Inject

import db.DBModule
import play.api.Logger
import play.api.db.slick.DatabaseConfigProvider

import scala.concurrent.{ExecutionContext, Future}

case class GatewayIdSchemeRow(id: String, empref: String)

trait GatewayIdSchemeModule extends DBModule {

  import driver.api._

  val GatewayIdSchemes = TableQuery[GatewayIdSchemeTable]

  class GatewayIdSchemeTable(tag: Tag) extends Table[GatewayIdSchemeRow](tag, "gateway_id_scheme") {
    def id = column[String]("id")

    def empref = column[String]("empref")

    def pk = primaryKey("gateway_id_scheme_pk", (id, empref))

    def * = (id, empref) <>(GatewayIdSchemeRow.tupled, GatewayIdSchemeRow.unapply)
  }

}

class GatewayIdSchemeDAO @Inject()(protected val dbConfigProvider: DatabaseConfigProvider)(implicit val ec: ExecutionContext) extends GatewayIdSchemeModule {

  import driver.api._

  def emprefsForId(gatewayId: String): Future[Seq[String]] = db.run {
    GatewayIdSchemes.filter(_.id === gatewayId).map(_.empref).result
  }

  def bindEmprefs(gatewayId: String, emprefs: List[String]): Future[Unit] =
    emprefsForId(gatewayId).flatMap { existingEmprefs =>
      Logger.info(s"exiting emprefs for $gatewayId: $existingEmprefs")
      val toInsert = emprefs.filter(e => !existingEmprefs.contains(e)).map(e => GatewayIdSchemeRow(gatewayId, e))
      Logger.info(s"to insert: $toInsert")
      db.run(GatewayIdSchemes ++= toInsert)
    }.map(_ => ())

}
