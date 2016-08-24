package uk.gov.bis.levyApiMock.mongo

import javax.inject.Inject

import play.api.libs.json._
import play.modules.reactivemongo.ReactiveMongoApi
import uk.gov.bis.levyApiMock.data.oauth2.{AuthRecord, AuthRecordOps}

import scala.concurrent.ExecutionContext

class AuthRecordMongo @Inject()(val mongodb: ReactiveMongoApi) extends MongoCollection[AuthRecord] with AuthRecordOps {

  implicit val authRecordR = Json.reads[AuthRecord]

  override val collectionName = "auth_records"

  override def find(accessToken: String)(implicit ec: ExecutionContext) = findOne("accessToken" -> accessToken)
}
