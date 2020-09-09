package uk.gov.bis.levyApiMock.mongo

import play.api.libs.json.Json.JsValueWrapper
import play.api.libs.json._
import play.modules.reactivemongo.ReactiveMongoApi
import reactivemongo.play.json.compat._
import reactivemongo.api.Cursor
import reactivemongo.play.json.collection.JSONCollection

import scala.concurrent.{ExecutionContext, Future}

trait MongoCollection[T] {
  def mongodb: ReactiveMongoApi

  def collectionName: String

  def collectionF(implicit ec: ExecutionContext): Future[JSONCollection] = mongodb.database.map(_.collection[JSONCollection](collectionName))

  def findMany(params: (String, JsValueWrapper)*)(implicit ec: ExecutionContext, reads: Reads[T]): Future[Seq[T]] = {
    val selector = Json.obj(params: _*)
    for {
      collection <- collectionF
      o <- collection.find(selector).cursor[JsObject]().collect[List](100, Cursor.FailOnError[List[JsObject]]())
    } yield o.flatMap {
      _.validate[T] match {
        case JsSuccess(resp, _) => Some(resp)
        case JsError(errs) => None
      }
    }
  }

  def findOne(params: (String, JsValueWrapper)*)(implicit ec: ExecutionContext, reads: Reads[T]): Future[Option[T]] = {
    val selector = Json.obj(params: _*)
    val of = for {
      collection <- collectionF
      o <- collection.find(selector).cursor[JsObject]().collect[List](1, Cursor.FailOnError[List[JsObject]]()).map(_.headOption)
    } yield o

    of.map {
      case Some(o) => o.validate[T] match {
        case JsSuccess(resp, _) => Some(resp)
        case JsError(errs) => None
      }
      case _ => None
    }
  }

  def remove(params: (String, JsValueWrapper)*)(implicit ec: ExecutionContext): Future[Unit] = {
    collectionF.map(coll => coll.delete().one(Json.obj(params: _*)))
  }
}
