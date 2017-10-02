package uk.gov.bis.userinfoApiMock.controllers

import javax.inject.Inject

import play.api.mvc.{Action, Controller}
import uk.gov.bis.oauth.actions.AuthorizedAction

import scala.concurrent.ExecutionContext

class UserInfoController @Inject()(AuthorizedAction: AuthorizedAction)(implicit ec: ExecutionContext) extends Controller {
  //noinspection TypeAnnotation
  def userinfo = Action.async { implicit request =>
    ???
  }
}

