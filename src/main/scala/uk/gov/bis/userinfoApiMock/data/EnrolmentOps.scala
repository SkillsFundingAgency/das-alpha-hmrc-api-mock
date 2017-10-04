package uk.gov.bis.userinfoApiMock.data

import com.google.inject.ImplementedBy
import uk.gov.bis.userinfoApiMock.models.Enrolments
import uk.gov.bis.userinfoApiMock.mongo.EnrolmentsMongo

import scala.concurrent.Future

@ImplementedBy(classOf[EnrolmentsMongo])
trait EnrolmentOps {
  def forGatewayID(gatewayID: String): Future[Option[Enrolments]]
}
