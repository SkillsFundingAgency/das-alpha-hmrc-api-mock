package uk.gov.bis.levyApiMock.data.levy

import scala.concurrent.{ExecutionContext, Future}

case class GatewayUser(gatewayID: String, password: String, empref: String, nameLine1: Option[String], nameLine2: Option[String])

trait GatewayUserOps {
  def forGatewayId(gatewayId: String)(implicit ec: ExecutionContext): Future[Option[GatewayUser]]
  def forEmpref(empref: String)(implicit ec: ExecutionContext): Future[Option[GatewayUser]]
}
