package uk.gov.bis.levyApiMock.mongo

import javax.inject.Inject

import play.api.libs.json._
import play.modules.reactivemongo.ReactiveMongoApi
import reactivemongo.play.json._
import reactivemongo.play.json.collection._
import uk.gov.bis.levyApiMock.data.oauth2.{AuthRecord, AuthRecordOps}

import scala.concurrent.{ExecutionContext, Future}

class AuthRecordMongo @Inject()(val mongodb: ReactiveMongoApi) extends AuthRecordOps {

  implicit val authRecordR = Json.reads[AuthRecord]

  def collectionF(implicit ec: ExecutionContext): Future[JSONCollection] = mongodb.database.map(_.collection[JSONCollection]("auth_records"))

  override def find(accessToken: String)(implicit ec: ExecutionContext): Future[Option[AuthRecord]] = {
    val of = for {
      collection <- collectionF
      o <- collection.find(Json.obj("accessToken" -> accessToken)).cursor[JsObject]().collect[List](1).map(_.headOption)
    } yield o

    of.map {
      case Some(o) => o.validate[AuthRecord] match {
        case JsSuccess(ar, _) => Some(ar)
        case JsError(errs) => None
      }
      case _ => None
    }
  }

  override def find(accessToken: String, empref: String)(implicit ec: ExecutionContext): Future[Option[AuthRecord]] = ???

  override def clearExpired()(implicit ec: ExecutionContext): Future[Unit] = ???

  override def expire(token: String)(implicit ec: ExecutionContext): Future[Int] = ???
}
