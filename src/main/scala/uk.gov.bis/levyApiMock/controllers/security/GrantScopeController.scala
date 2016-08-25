package uk.gov.bis.levyApiMock.controllers.security

import javax.inject.Inject

import play.api.Logger
import play.api.mvc.{Action, Controller}
import uk.gov.bis.levyApiMock.TaxServiceConfig

class GrantScopeController @Inject()() extends Controller {

  def show(authId: String) = Action { implicit request =>
    Redirect(TaxServiceConfig.config.baseURI + s"/gateway/sign-in?continue=/oauth/grantscope?auth_id=$authId&origin=oauth-frontend")
  }

}
