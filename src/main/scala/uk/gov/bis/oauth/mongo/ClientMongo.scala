package uk.gov.bis.oauth.mongo

import javax.inject.Inject

import play.api.libs.json.Json
import play.modules.reactivemongo.ReactiveMongoApi
import uk.gov.bis.mongo.MongoCollection
import uk.gov.bis.oauth.data.{Application, ClientOps}
import uk.gov.bis.utils.TimeSource

import scala.concurrent.{ExecutionContext, Future}

class ClientMongo @Inject()(val mongodb: ReactiveMongoApi, val timeSource: TimeSource) extends MongoCollection[Application] with ClientOps {
  implicit val fmt = Json.format[Application]

  override def collectionName: String = "applications"

  override def forId(clientID: String)(implicit ec: ExecutionContext): Future[Option[Application]] = findOne("clientID" -> clientID)
}
