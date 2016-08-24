package uk.gov.bis.levyApiMock.mongo

import play.api.libs.json.Json.JsValueWrapper
import play.api.libs.json._
import reactivemongo.play.json._
import reactivemongo.play.json.collection.JSONCollection

import scala.concurrent.{ExecutionContext, Future}

trait MongoCollection[T] {
  def collectionF(implicit ec: ExecutionContext): Future[JSONCollection]

  def findOne(params: (String, JsValueWrapper)*)(implicit ec: ExecutionContext, reads: Reads[T]): Future[Option[T]] = {
    val selector = Json.obj(params: _*)
    val of = for {
      collection <- collectionF
      o <- collection.find(selector).cursor[JsObject]().collect[List](1).map(_.headOption)
    } yield o

    of.map {
      case Some(o) => o.validate[T] match {
        case JsSuccess(resp, _) => Some(resp)
        case JsError(errs) => None
      }
      case _ => None
    }
  }
}
