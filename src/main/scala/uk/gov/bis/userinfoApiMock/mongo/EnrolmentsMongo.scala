package uk.gov.bis.userinfoApiMock.mongo

import javax.inject.Inject

import play.modules.reactivemongo.ReactiveMongoApi
import uk.gov.bis.mongo.MongoCollection
import uk.gov.bis.userinfoApiMock.data.EnrolmentOps
import uk.gov.bis.userinfoApiMock.models.{Enrolment, Enrolments}
import uk.gov.bis.utils.TimeSource

import scala.concurrent.{ExecutionContext, Future}

class EnrolmentsMongo @Inject()(val mongodb: ReactiveMongoApi, val timeSource: TimeSource)(implicit ec: ExecutionContext) extends MongoCollection[Enrolments] with EnrolmentOps {
  override def collectionName = "hmrc_enrolments"

  override def forGatewayID(gatewayID: String): Future[Option[Enrolments]] = findOne("gatewayID" -> gatewayID)
}
