package controllers.admin

import javax.inject.Inject

import data.oauth2.AuthRecordOps
import play.api.mvc.{Action, Controller}

import scala.concurrent.ExecutionContext

class UIController @Inject()(authRecords: AuthRecordOps)(implicit ec: ExecutionContext) extends Controller {

  def index = Action.async { implicit request =>
    authRecords.all().map(rows => Ok(views.html.index(rows.sortBy(_.accessToken))))
  }

  def expireToken(token: String) = Action.async { implicit request =>
    authRecords.expire(token).map(_ => Redirect(controllers.admin.routes.UIController.index()))
  }

}
