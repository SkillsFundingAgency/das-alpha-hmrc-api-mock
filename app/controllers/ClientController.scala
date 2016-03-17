package controllers

import javax.inject.Inject

import actions.client.ClientUserAction
import db.client.{DASUserDAO, SchemeClaimDAO, SchemeDAO}
import play.api.data.Form
import play.api.data.Forms._
import play.api.mvc.Controller

import scala.concurrent.{ExecutionContext, Future}

case class UserData(name: String, password: String)

class ClientController @Inject()(schemeDAO: SchemeDAO, dasUserDAO: DASUserDAO, UserAction: ClientUserAction, schemeClaimDAO: SchemeClaimDAO)(implicit exec: ExecutionContext) extends Controller {
  def index = UserAction.async { request =>
    schemeClaimDAO.forUser(request.user.id).map { claimedSchemes =>
      Ok(views.html.client.index(request.user, claimedSchemes))
    }
  }

  val claimMapping = Form(
    "empref" -> text
  )


  def claimScheme = UserAction.async { implicit request =>
    claimMapping.bindFromRequest().fold(
      formWithErrors => Future.successful(Redirect(controllers.routes.ClientController.index())),
      empref => handleClaim(empref).map(_ => Redirect(controllers.routes.ClientController.index()))
    )
  }

  def handleClaim(empref: String): Future[Boolean] = ???

}
