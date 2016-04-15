package data.levy

import scala.concurrent.{ExecutionContext, Future}

case class GatewayIdScheme(id: String, empref: String)

trait GatewayIdSchemeOps {
  def emprefsForId(gatewayId: String)(implicit ec: ExecutionContext): Future[Seq[String]]

  def bindEmprefs(gatewayId: String, emprefs: List[String])(implicit ec: ExecutionContext): Future[Unit]
}
