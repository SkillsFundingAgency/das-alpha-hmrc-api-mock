package uk.gov.bis.levyApiMock.mongo

import javax.inject.Inject

import play.api.libs.json.{JsObject, Json}
import play.modules.reactivemongo.ReactiveMongoApi
import reactivemongo.play.json._
import uk.gov.bis.levyApiMock.data.{Application, ClientOps}

import scala.concurrent.{ExecutionContext, Future}

class ClientMongo @Inject()(val mongodb: ReactiveMongoApi) extends MongoCollection[Application] with ClientOps {
  implicit val fmt = Json.format[Application]

  override def collectionName: String = "applications"

  override def validate(id: String, secret: Option[String], grantType: String)(implicit ec: ExecutionContext): Future[Boolean] = {
    for {
      coll <- collectionF
      o <- coll.find(Json.obj("clientID" -> id, "clientSecret" -> secret)).cursor[JsObject]().collect[List](1).map(_.nonEmpty)
    } yield o
  }

  override def forId(clientID: String)(implicit ec: ExecutionContext): Future[Option[Application]] = findOne("clientID" -> clientID)
}
