package uk.gov.bis.levyApiMock.mongo

import javax.inject.Inject

import play.modules.reactivemongo.ReactiveMongoApi
import uk.gov.bis.levyApiMock.auth.OAuthTrace
import uk.gov.bis.levyApiMock.data.oauth2.{AuthRecord, AuthRecordOps}
import play.api.libs.json._
import reactivemongo.play.json.compat._
import json2bson._

import scala.concurrent.{ExecutionContext, Future}

class AuthRecordMongo @Inject()(val mongodb: ReactiveMongoApi) extends MongoCollection[AuthRecord] with AuthRecordOps {

  implicit val authRecordF = Json.format[AuthRecord]

  override val collectionName = "sys_auth_records"

  override def find(accessToken: String)(implicit ec: ExecutionContext) = findOne("accessToken" -> accessToken)

  override def forRefreshToken(refreshToken: String)(implicit ec: ExecutionContext) = findOne("refreshToken" -> refreshToken)

  override def forAccessToken(accessToken: String)(implicit ec: ExecutionContext) = findOne("accessToken" -> accessToken)

  override def find(gatewayID: String, clientId: Option[String])(implicit ec: ExecutionContext) = findOne("gatewayID" -> gatewayID, "clientID" -> clientId)

  override def create(auth: AuthRecord)(implicit ec: ExecutionContext): Future[Unit] = {
    for {
      collection <- collectionF
      r <- collection.insert(ordered = false).one(auth)
    } yield ()
  }

  override def deleteExistingAndCreate(existing: AuthRecord, created: AuthRecord)(implicit ec: ExecutionContext): Future[Unit] = {
    OAuthTrace(s"Removing ${existing.accessToken} and creating ${created.accessToken}")
    for {
      coll <- collectionF
      _ <- coll.delete().one(Json.obj("accessToken" -> existing.accessToken))
      _ <- coll.insert(ordered = false).one(created)
    } yield ()
  }
}
