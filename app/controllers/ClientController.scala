package controllers

import javax.inject.Inject
import db.client.{DASUserDAO, SchemeDAO}
import play.api.data.Form
import play.api.data.Forms._
import play.api.mvc.{Action, Controller}

import scala.concurrent.{Future, ExecutionContext}

case class UserData(name: String, password: String)

class ClientController @Inject()(schemeDAO: SchemeDAO, dasUserDAO: DASUserDAO)(implicit exec: ExecutionContext) extends Controller {

  val userForm = Form(
    mapping(
      "username" -> text,
      "password" -> text
    )(UserData.apply)(UserData.unapply)
  )

  def index = Action.async {
    schemeDAO.all().map(schemes => Ok(views.html.index(schemes)))
  }

  def showLogin = Action {
    Ok(views.html.client.login(userForm))
  }

  def handleLogin = Action.async { implicit request =>
    userForm.bindFromRequest.fold(
      formWithErrors => Future.successful(BadRequest(views.html.client.login(formWithErrors))),
      userData => {
        dasUserDAO.validate(userData.name, userData.password).map {
          case Some(user) => Redirect(routes.ClientController.index()).withSession(("userId", user.id.toString))
          case None => Ok(views.html.client.login(userForm.withError("username", "Bad user name or password")))
        }
      }
    )
  }
}
