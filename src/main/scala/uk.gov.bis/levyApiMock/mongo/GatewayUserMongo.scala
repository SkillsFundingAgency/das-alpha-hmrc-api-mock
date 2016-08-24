package uk.gov.bis.levyApiMock.mongo

import javax.inject._

import play.api.libs.json.Json
import play.modules.reactivemongo._
import uk.gov.bis.levyApiMock.data.levy._

import scala.concurrent.{ExecutionContext, Future}

class GatewayUserMongo @Inject()(val mongodb: ReactiveMongoApi) extends MongoCollection[GatewayUser] with GatewayUserOps {

  implicit val userR = Json.reads[GatewayUser]

  override val collectionName = "gateway_users"

  override def forGatewayID(gatewayId: String)(implicit ec: ExecutionContext) = findOne("gatewayID" -> gatewayId)

  override def forEmpref(empref: String)(implicit ec: ExecutionContext): Future[Option[GatewayUser]] = findOne("empref" -> empref)
}
