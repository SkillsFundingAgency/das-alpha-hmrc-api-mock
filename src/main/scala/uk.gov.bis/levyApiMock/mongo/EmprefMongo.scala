package uk.gov.bis.levyApiMock.mongo

import javax.inject._

import play.api.libs.json.{JsError, JsObject, JsSuccess, Json}
import play.modules.reactivemongo._
import reactivemongo.play.json._
import reactivemongo.play.json.collection._
import uk.gov.bis.levyApiMock.data.levy._

import scala.concurrent.{ExecutionContext, Future}

case class EmployerName(nameLine1: Option[String], nameLine2: Option[String])

case class Employer(name: EmployerName)

case class Href(href: String)

case class EmprefResponse(_links: Map[String, Href], empref: String, employer: Employer)

class EmprefMongo @Inject()(val mongodb: ReactiveMongoApi) extends EmprefOps {

  implicit val empNameR = Json.reads[EmployerName]
  implicit val employerR = Json.reads[Employer]
  implicit val hrefR = Json.reads[Href]
  implicit val respR = Json.reads[EmprefResponse]

  def collectionF(implicit ec: ExecutionContext): Future[JSONCollection] = mongodb.database.map(_.collection[JSONCollection]("emprefs"))

  override def forEmpref(empref: String)(implicit ec: ExecutionContext): Future[Option[EmprefResponse]] = {
    val of = for {
      collection <- collectionF
      o <- collection.find(Json.obj("empref" -> empref)).cursor[JsObject]().collect[List](1).map(_.headOption)
    } yield o

    of.map {
      case Some(o) => o.validate[EmprefResponse] match {
        case JsSuccess(resp, _) => Some(resp)
        case JsError(errs) => None
      }
      case _ => None
    }
  }
}
