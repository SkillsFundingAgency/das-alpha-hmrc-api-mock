package uk.gov.bis.levyApiMock.mongo

import javax.inject._

import play.api.libs.json._
import play.modules.reactivemongo._
import reactivemongo.play.json.collection._
import uk.gov.bis.levyApiMock.data.levy._

import scala.concurrent.{ExecutionContext, Future}

class EmprefMongo @Inject()(val mongodb: ReactiveMongoApi) extends MongoCollection[EmprefResponse] with EmprefOps {
  implicit val empNameR = Json.reads[EmployerName]
  implicit val employerR = Json.reads[Employer]
  implicit val hrefR = Json.reads[Href]
  implicit val respR = Json.reads[EmprefResponse]

  def collectionF(implicit ec: ExecutionContext): Future[JSONCollection] = mongodb.database.map(_.collection[JSONCollection]("emprefs"))

  override def forEmpref(empref: String)(implicit ec: ExecutionContext) = findOne("empref" -> empref)
}
