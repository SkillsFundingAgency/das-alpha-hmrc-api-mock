package uk.gov.bis.oauth.mongo

import javax.inject.Inject

import play.api.libs.json.Json
import play.modules.reactivemongo.ReactiveMongoApi
import reactivemongo.play.json._
import uk.gov.bis.mongo.MongoCollection
import uk.gov.bis.oauth.data
import uk.gov.bis.oauth.data.{AuthCodeOps, AuthCodeRow}
import uk.gov.bis.utils.TimeSource

import scala.concurrent.{ExecutionContext, Future}

class AuthCodeMongo @Inject()(val mongodb: ReactiveMongoApi, timeSource: TimeSource) extends MongoCollection[AuthCodeRow] with AuthCodeOps {
  implicit val fmt = Json.format[AuthCodeRow]

  override val collectionName: String = "sys_auth_codes"

  override def find(code: String)(implicit ec: ExecutionContext) = findOne("authorizationCode" -> code)

  override def delete(code: String)(implicit ec: ExecutionContext): Future[Int] = {
    for {
      coll <- collectionF
      i <- coll.remove(Json.obj("authorizationCode" -> code))
    } yield i.n
  }

  override def create(code: String, gatewayUserId: String, redirectUri: String, clientId: String, scope: String)(implicit ec: ExecutionContext): Future[Int] = {
    val row = data.AuthCodeRow(code, gatewayUserId, redirectUri, timeSource.currentTimeMillis(), Some("read:apprenticeship-levy"), Some(clientId), 3600)
    for {
      coll <- collectionF
      i <- coll.insert(row)
    } yield i.n
  }
}
