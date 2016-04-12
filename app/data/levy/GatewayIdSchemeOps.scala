package data.levy

import scala.concurrent.Future

case class GatewayIdScheme(id: String, empref: String)

trait GatewayIdSchemeOps {
  def emprefsForId(gatewayId: String): Future[Seq[String]]

  def bindEmprefs(gatewayId: String, emprefs: List[String]): Future[Unit]
}
