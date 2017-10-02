package uk.gov.bis.oauth.mongo

import javax.inject.Inject

import play.api.libs.json._
import play.modules.reactivemongo.ReactiveMongoApi
import reactivemongo.play.json._
import uk.gov.bis.mongo.MongoCollection
import uk.gov.bis.oauth.auth.OAuthTrace
import uk.gov.bis.oauth.data.{AuthRecord, AuthRecordOps}

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
      r <- collection.insert(auth)
    } yield ()
  }

  override def deleteExistingAndCreate(existing: AuthRecord, created: AuthRecord)(implicit ec: ExecutionContext): Future[Unit] = {
    OAuthTrace(s"Removing ${existing.accessToken} and creating ${created.accessToken}")
    for {
      coll <- collectionF
      _ <- coll.remove(Json.obj("accessToken" -> existing.accessToken))
      _ <- coll.insert(created)
    } yield ()
  }
}
