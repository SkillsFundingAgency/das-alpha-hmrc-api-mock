package uk.gov.bis.levyApiMock.controllers.security

import javax.inject.Inject

import play.api.mvc.{Action, Controller}
import uk.gov.bis.levyApiMock.Config.config.taxservice

class GrantScopeController @Inject()() extends Controller {

  def show(authId: Long) = Action { implicit request =>
    Redirect(taxservice.baseURI + s"/gg/sign-in?continue=/oauth/grantscope?auth_id=$authId&origin=oauth-frontend")
  }

}
