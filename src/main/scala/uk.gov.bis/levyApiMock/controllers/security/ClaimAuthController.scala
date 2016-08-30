package uk.gov.bis.levyApiMock.controllers.security

import javax.inject.{Inject, Singleton}

import play.api.Logger
import play.api.mvc.{Action, AnyContent, Controller}
import uk.gov.bis.levyApiMock.actions._
import uk.gov.bis.levyApiMock.auth.generateToken
import uk.gov.bis.levyApiMock.data.{AuthCodeOps, AuthId, AuthIdOps}
import views.html.helper

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class ClaimAuthController @Inject()(GatewayAction: GatewayUserAction, authCodes: AuthCodeOps, authIds: AuthIdOps)(implicit ec: ExecutionContext) extends Controller {

  /**
    * Handle the initial oAuth request
    */
  def authorize(scope: Option[String], clientId: String, redirectUri: String, state: Option[String]) = Action.async { implicit request =>
    scope match {
      case Some(s) =>
        authIds.stash(AuthId(s, clientId, redirectUri, state)).map { id =>
          Logger.debug(s"stashed with id %id")
          val show = routes.GrantScopeController.show(id)
          Logger.debug(s"redirecting to ${show.url}")
          Redirect(show)
        }
      case None => Future.successful(BadRequest("missing scope"))
    }
  }

  def createAuthCode(scope: String, clientId: String, redirectUri: String, state: Option[String], request: GatewayUserRequest[AnyContent]): Future[String] = {
    val authCode = generateToken

    authCodes.create(authCode, request.user.gatewayID, redirectUri, clientId, scope).map { _ =>
      state match {
        case Some(s) => s"$redirectUri?code=$authCode&state=${helper.urlEncode(s)}"
        case None => s"$redirectUri?code=$authCode"
      }
    }
  }

}
