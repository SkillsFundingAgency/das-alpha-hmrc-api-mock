package uk.gov.bis.levyApiMock.mongo

import javax.inject.Inject

import play.api.libs.json.Json
import play.modules.reactivemongo.ReactiveMongoApi
import uk.gov.bis.levyApiMock.data.{Scope, ScopeOps}

import scala.concurrent.ExecutionContext

class ScopeMongo @Inject()(val mongodb: ReactiveMongoApi) extends MongoCollection[Scope] with ScopeOps {
  implicit val scopeR = Json.reads[Scope]

  override def collectionName: String = "sys_scopes"

  override def byName(name: String)(implicit ec: ExecutionContext) = findOne("name" -> name)
}
