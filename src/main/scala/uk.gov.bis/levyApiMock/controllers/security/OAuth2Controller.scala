package uk.gov.bis.levyApiMock.controllers.security

import javax.inject.{Singleton, Inject}

import uk.gov.bis.levyApiMock.auth.APIDataHandler
import play.api.mvc.{Action, Controller}

import scala.concurrent.ExecutionContext
import scalaoauth2.provider._

trait MyOAuth extends OAuth2Provider {
  override val tokenEndpoint: TokenEndpoint = new TokenEndpoint {
    override val handlers: Map[String, GrantHandler] = Map(
      OAuthGrantType.AUTHORIZATION_CODE -> new AuthorizationCode,
      OAuthGrantType.REFRESH_TOKEN -> new RefreshToken,
      OAuthGrantType.CLIENT_CREDENTIALS -> new ClientCredentials
    )
  }
}

@Singleton
class OAuth2Controller @Inject()(dataHandler: APIDataHandler)(implicit exec: ExecutionContext) extends Controller with MyOAuth {
  def accessToken = Action.async { implicit request =>
    issueAccessToken(dataHandler)
  }
}
