package uk.gov.bis.data.levy

import scala.concurrent.{ExecutionContext, Future}

case class ServiceBinding(service: String, identifierType: String, identifier: String)

case class Enrolment(gatewayId: String, service: String, identifierType: String, taxId: String)

trait EnrolmentOps {
  def forGatewayId(gatewayId: String)(implicit ec: ExecutionContext): Future[Seq[ServiceBinding]]

  def bindEnrolments(gatewayId: String, enrolments: List[ServiceBinding])(implicit ec: ExecutionContext): Future[Unit]
}
