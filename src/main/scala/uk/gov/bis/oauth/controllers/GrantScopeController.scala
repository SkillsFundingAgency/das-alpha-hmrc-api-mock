package uk.gov.bis.oauth.controllers

import play.api.mvc.{Action, Controller}
import uk.gov.bis.levyApiMock.Config.config.taxservice

class GrantScopeController() extends Controller {

  def show(authId: Long) = Action { implicit request =>
    Redirect(taxservice.baseURI + s"/gg/sign-in?continue=/oauth/grantscope?auth_id=$authId&origin=oauth-frontend")
  }

}
