package uk.gov.bis.userinfoApiMock.data

import javax.inject.Inject

import com.google.inject.ImplementedBy
import uk.gov.bis.oauth.mongo.GatewayUserMongo
import uk.gov.bis.userinfoApiMock.models.UserInfo

import scala.concurrent.{ExecutionContext, Future}

@ImplementedBy(classOf[UserInfoServiceImpl])
trait UserInfoService {
  def forGatewayID(gatewayID: String, scopes: Seq[String]): Future[Option[UserInfo]]
}

class UserInfoServiceImpl @Inject()(gatewayUserMongo: GatewayUserMongo, enrolmentOps: EnrolmentOps)(implicit ec:ExecutionContext) extends UserInfoService {
  override def forGatewayID(gatewayID: String, scopes: Seq[String]): Future[Option[UserInfo]] = {
    gatewayUserMongo.forGatewayID(gatewayID).flatMap {
      case None    => Future.successful(None)
      case Some(_) =>
        if (!scopes.contains("openid:hmrc_enrolments")) {
          Future.successful(None)
        } else {
          enrolmentOps.forGatewayID(gatewayID).map(enrolments => Some(UserInfo(hmrc_enrolments = Some(enrolments))))
        }
    }
  }
}