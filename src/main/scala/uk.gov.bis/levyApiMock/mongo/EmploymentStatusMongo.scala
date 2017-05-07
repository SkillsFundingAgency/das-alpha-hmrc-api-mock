package uk.gov.bis.levyApiMock.mongo

import javax.inject._

import play.modules.reactivemongo._
import uk.gov.bis.levyApiMock.data.levy.EmploymentCheckRecord
import uk.gov.bis.levyApiMock.services.EmploymentStatusRepo

import scala.concurrent.{ExecutionContext, Future}

class EmploymentStatusMongo @Inject()(val mongodb: ReactiveMongoApi)(implicit ec: ExecutionContext) extends MongoCollection[EmploymentCheckRecord] with EmploymentStatusRepo[Future] {
  override val collectionName = "employment_check"

  override def find(empref: String, nino: String) = findMany("empref" -> empref, "nino" -> nino)
}




