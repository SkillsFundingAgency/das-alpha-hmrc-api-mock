package db.levy

import javax.inject.Inject

import data.levy.{GatewayIdSchemeOps, GatewayIdScheme}
import db.DBModule
import play.api.db.slick.DatabaseConfigProvider

import scala.concurrent.{ExecutionContext, Future}

trait GatewayIdSchemeModule extends DBModule {

  import driver.api._

  val GatewayIdSchemes = TableQuery[GatewayIdSchemeTable]

  class GatewayIdSchemeTable(tag: Tag) extends Table[GatewayIdScheme](tag, "gateway_id_scheme") {
    def id = column[String]("id")

    def empref = column[String]("empref")

    def pk = primaryKey("gateway_id_scheme_pk", (id, empref))

    def * = (id, empref) <>(GatewayIdScheme.tupled, GatewayIdScheme.unapply)
  }

}

class GatewayIdSchemeDAO @Inject()(protected val dbConfigProvider: DatabaseConfigProvider)(implicit val ec: ExecutionContext)
  extends GatewayIdSchemeModule with GatewayIdSchemeOps {

  import driver.api._

  def emprefsForId(gatewayId: String): Future[Seq[String]] = db.run(GatewayIdSchemes.filter(_.id === gatewayId).map(_.empref).result)

  /**
    * Replace existing list of emprefs held for the gatewayId with the new list
    */
  def bindEmprefs(gatewayId: String, emprefs: List[String]): Future[Unit] = run {
    for {
      _ <- GatewayIdSchemes.filter(_.id === gatewayId).delete
      _ <- GatewayIdSchemes ++= emprefs.map(e => GatewayIdScheme(gatewayId, e))
    } yield ()
  }

}
