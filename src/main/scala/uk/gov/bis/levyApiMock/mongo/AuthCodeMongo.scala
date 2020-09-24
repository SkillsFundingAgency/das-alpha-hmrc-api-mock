package uk.gov.bis.levyApiMock.mongo

import javax.inject.Inject

import play.modules.reactivemongo.ReactiveMongoApi
import uk.gov.bis.levyApiMock.data.{AuthCodeOps, AuthCodeRow, TimeSource}
import play.api.libs.json._
import reactivemongo.play.json.compat._
import json2bson._

import scala.concurrent.{ExecutionContext, Future}

class AuthCodeMongo @Inject()(val mongodb: ReactiveMongoApi, timeSource: TimeSource) extends MongoCollection[AuthCodeRow] with AuthCodeOps {
  implicit val fmt = Json.format[AuthCodeRow]

  override val collectionName: String = "sys_auth_codes"

  override def find(code: String)(implicit ec: ExecutionContext) = findOne("authorizationCode" -> code)

  override def delete(code: String)(implicit ec: ExecutionContext): Future[Int] = {
    for {
      coll <- collectionF
      i <- coll.delete().one(Json.obj("authorizationCode" -> code))
    } yield i.n
  }

  override def create(code: String, gatewayUserId: String, redirectUri: String, clientId: String, scope: String)(implicit ec: ExecutionContext): Future[Int] = {
    val row = AuthCodeRow(code, gatewayUserId, redirectUri, timeSource.currentTimeMillis(), Some("read:apprenticeship-levy"), Some(clientId), 3600)
    for {
      coll <- collectionF
      i <- coll.insert(ordered = false).one(row)
    } yield i.n
  }
}
