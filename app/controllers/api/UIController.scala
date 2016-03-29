package controllers.api

import javax.inject.{Inject, Singleton}

import db.outh2.AccessTokenDAO
import play.api.Logger
import play.api.mvc.{Action, Controller}

import scala.concurrent.ExecutionContext

@Singleton
class UIController @Inject()(accessTokens: AccessTokenDAO)(implicit ec: ExecutionContext) extends Controller {

  def index = Action.async { implicit request =>
    accessTokens.all().map(rows => Ok(views.html.index(rows)))
  }

}
