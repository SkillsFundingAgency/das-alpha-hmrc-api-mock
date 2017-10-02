package uk.gov.bis.oauth.actions

import uk.gov.bis.oauth.data.{GatewayUser, GatewayUserOps}

import scala.concurrent.{ExecutionContext, Future}

trait StubGatewayUsers extends GatewayUserOps {
  override def forGatewayID(gatewayID: String)(implicit ec: ExecutionContext): Future[Option[GatewayUser]] = ???

  override def forEmpref(empref: String)(implicit ec: ExecutionContext): Future[Option[GatewayUser]] = ???

  override def validate(gatewayID: String, password: String)(implicit ec: ExecutionContext): Future[Option[GatewayUser]] = ???
}
