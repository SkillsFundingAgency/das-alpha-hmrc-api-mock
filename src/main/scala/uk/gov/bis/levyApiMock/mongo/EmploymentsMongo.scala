package uk.gov.bis.levyApiMock.mongo

import javax.inject._

import play.modules.reactivemongo._
import uk.gov.bis.levyApiMock.data.EmploymentCheckRecord
import uk.gov.bis.levyApiMock.services.EmploymentsRepo
import uk.gov.bis.mongo.MongoCollection

import scala.concurrent.{ExecutionContext, Future}

class EmploymentsMongo @Inject()(val mongodb: ReactiveMongoApi)(implicit ec: ExecutionContext) extends MongoCollection[EmploymentCheckRecord] with EmploymentsRepo[Future] {
  override val collectionName = "employments"

  override def find(empref: String, nino: String) = findMany("empref" -> empref, "nino" -> nino)
}




