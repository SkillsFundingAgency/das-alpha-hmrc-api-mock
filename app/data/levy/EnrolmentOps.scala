package data.levy

import scala.concurrent.{ExecutionContext, Future}

case class ServiceBinding(identifierType: String, taxId: String)

case class Enrolment(gatewayId: String, identifierType: String, taxId: String)

trait EnrolmentOps {
  def enrolmentsForGatewayId(gatewayId: String)(implicit ec: ExecutionContext): Future[Seq[ServiceBinding]]

  def bindEnrolments(gatewayId: String, enrolments: List[ServiceBinding])(implicit ec: ExecutionContext): Future[Unit]
}
