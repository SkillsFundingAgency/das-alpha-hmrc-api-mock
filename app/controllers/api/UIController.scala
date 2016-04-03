package controllers.api

import javax.inject.{Inject, Singleton}

import db.outh2.AuthRecordOps
import play.api.mvc.{Action, Controller}

import scala.concurrent.ExecutionContext

@Singleton
class UIController @Inject()(authRecords: AuthRecordOps)(implicit ec: ExecutionContext) extends Controller {

  def index = Action.async { implicit request =>
    authRecords.all().map(rows => Ok(views.html.index(rows)))
  }

}
