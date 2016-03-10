package controllers

import javax.inject.Inject

import db.SchemeDAO
import play.api.mvc.{Action, Controller}

import scala.concurrent.ExecutionContext

class Application @Inject()(schemeDAO: SchemeDAO)(implicit exec: ExecutionContext) extends Controller {

  def index = Action.async {
    schemeDAO.all().map(schemes => Ok(views.html.index(schemes)))
  }

}
