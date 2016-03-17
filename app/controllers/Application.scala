package controllers

import javax.inject.Inject
import db.levy.SchemeDAO
import db.outh2.UserDAO
import play.api.data.Form
import play.api.data.Forms._
import play.api.mvc.{Action, Controller}

import scala.concurrent.ExecutionContext

class Application @Inject()(schemeDAO: SchemeDAO, userDAO: UserDAO)(implicit exec: ExecutionContext) extends Controller {

  def index = Action.async {
    schemeDAO.all().map(schemes => Ok(views.html.index(schemes)))
  }

  val userForm = Form(
    tuple(
      "username" -> text,
      "password" -> text
    )
  )

  def handleOauth2Login = Action.async { implicit request =>
    val bound = userForm.bindFromRequest
    bound.errors match {
      case Seq() => ???
      case errs => ???
    }
    val (username, password) = userForm.bindFromRequest.get

    ???
  }
}
