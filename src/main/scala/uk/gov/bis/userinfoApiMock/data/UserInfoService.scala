package uk.gov.bis.userinfoApiMock.data

import javax.inject.Inject

import com.google.inject.ImplementedBy
import play.api.Logger
import uk.gov.bis.oauth.mongo.GatewayUserMongo
import uk.gov.bis.userinfoApiMock.models.{GovernmentGateway, UserInfo}

import scala.concurrent.{ExecutionContext, Future}

@ImplementedBy(classOf[UserInfoServiceImpl])
trait UserInfoService {
  def forGatewayID(gatewayID: String, scopes: Seq[String]): Future[Option[UserInfo]]
}

class UserInfoServiceImpl @Inject()(gatewayUserMongo: GatewayUserMongo, enrolmentOps: EnrolmentOps)(implicit ec: ExecutionContext) extends UserInfoService {
  override def forGatewayID(gatewayID: String, scopes: Seq[String]): Future[Option[UserInfo]] = {
    Logger.debug(s"Looking for userinfo for $gatewayID with scopes $scopes")
    gatewayUserMongo.forGatewayID(gatewayID).flatMap {
      case None    => Future.successful(None)
      case Some(_) =>
        scopes.foldLeft(Future.successful(UserInfo())) { (ui, scope) =>
          scopeHandlers.get(scope) match {
            case None          => ui
            case Some(handler) =>
              Logger.debug(s"Found handler for $scope")
              ui.flatMap(handler(gatewayID, _))
          }
        }.map(Some(_))
    }
  }

  def populateEnrolments(gatewayID: String, userInfo: UserInfo): Future[UserInfo] =
    enrolmentOps.forGatewayID(gatewayID).map {
      case None     => userInfo
      case Some(es) => userInfo.copy(hmrc_enrolments = Some(es.enrolments))
    }

  def populateGovernmentGateway(gatewayID: String, userInfo: UserInfo): Future[UserInfo] =
    Future.successful(userInfo.copy(government_gateway = Some(GovernmentGateway(user_id = Some(gatewayID)))))

  type ScopeHandler = (String, UserInfo) => Future[UserInfo]

  private val scopeHandlers = Map[String, ScopeHandler](
    "openid:hmrc_enrolments" -> populateEnrolments,
    "openid:government_gateway" -> populateGovernmentGateway
  )
}