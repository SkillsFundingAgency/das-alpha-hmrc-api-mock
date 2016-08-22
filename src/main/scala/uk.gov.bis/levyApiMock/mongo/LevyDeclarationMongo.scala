package uk.gov.bis.levyApiMock.mongo

import javax.inject._

import play.api.libs.json.{JsObject, Json}
import play.modules.reactivemongo._
import reactivemongo.play.json._
import reactivemongo.play.json.collection._
import uk.gov.bis.levyApiMock.data.levy.LevyDeclarationOps
import uk.gov.bis.levyApiMock.models.LevyDeclarations

import scala.concurrent.{ExecutionContext, Future}

class LevyDeclarationMongo @Inject()(val mongodb: ReactiveMongoApi) extends LevyDeclarationOps {

  def collectionF(implicit ec:ExecutionContext): Future[JSONCollection] = mongodb.database.map(_.collection[JSONCollection]("declarations"))

  override def byEmpref(empref: String)(implicit ec: ExecutionContext): Future[Option[LevyDeclarations]] = {
    val of = for {
      collection <- collectionF
      o <- collection.find(Json.obj("empref" -> empref)).cursor[JsObject]().collect[List]().map(_.headOption)
    } yield o

    of.map {
      case Some(o) => o.validate[LevyDeclarations].asOpt
      case _ => None
    }
  }
}
