package controllers

import javax.inject.Inject

import auth.DASDataHandler
import play.api.mvc.{Action, Controller}

import scala.concurrent.ExecutionContext
import scalaoauth2.provider._

trait MyOAuth extends OAuth2Provider {
  override val tokenEndpoint: TokenEndpoint = new TokenEndpoint {
    override val handlers: Map[String, GrantHandler] = Map(
      OAuthGrantType.AUTHORIZATION_CODE -> new AuthorizationCode,
      OAuthGrantType.CLIENT_CREDENTIALS -> new ClientCredentials
    )
  }
}

class OAuth2Controller @Inject()(dataHandler: DASDataHandler)(implicit exec: ExecutionContext) extends Controller with MyOAuth {
  def accessToken = Action.async { implicit request =>
    issueAccessToken(dataHandler)
  }
}
