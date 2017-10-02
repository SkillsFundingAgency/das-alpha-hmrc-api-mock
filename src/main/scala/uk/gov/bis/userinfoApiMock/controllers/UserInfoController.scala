package uk.gov.bis.userinfoApiMock.controllers

import javax.inject.Inject

import play.api.Logger
import play.api.libs.json.Json
import play.api.mvc.Controller
import uk.gov.bis.oauth.actions.AuthenticatedAction
import uk.gov.bis.userinfoApiMock.data.UserInfoService

import scala.concurrent.{ExecutionContext, Future}

class UserInfoController @Inject()(userInfo: UserInfoService, authenticatedAction: AuthenticatedAction)(implicit ec: ExecutionContext) extends Controller {
  //noinspection TypeAnnotation
  def userinfo = authenticatedAction.async { implicit request =>
    val scopes = request.authRecord.scope.getOrElse("").split(" ").toList.filter(_.trim != "")

    if (!scopes.contains("openid")) {
      Future.successful(Unauthorized("No 'openid' scope is associated with bearer token"))
    } else {
      Logger.debug(s"Gateway id is ${request.authRecord.gatewayID}")
      userInfo.forGatewayID(request.authRecord.gatewayID, scopes).map {
        case None     => NotFound
        case Some(ui) => Ok(Json.toJson(ui))
      }
    }
  }
}

