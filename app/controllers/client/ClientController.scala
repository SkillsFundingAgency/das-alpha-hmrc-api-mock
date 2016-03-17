package controllers.client

import java.sql.Date
import javax.inject.Inject

import actions.client.ClientUserAction
import cats.data.OptionT
import cats.std.future._
import db.client.{SchemeClaimRow, DASUserDAO, SchemeClaimDAO, SchemeDAO}
import db.outh2.{AuthCodeRow, AuthCodeDAO}
import org.joda.time.DateTime
import play.api.data.Form
import play.api.data.Forms._
import play.api.libs.json.Json
import play.api.libs.ws.WSClient
import play.api.mvc.{RequestHeader, Headers, Controller}

import scala.concurrent.{ExecutionContext, Future}

class ClientController @Inject()(ws: WSClient, schemeDAO: SchemeDAO, dasUserDAO: DASUserDAO, UserAction: ClientUserAction, schemeClaimDAO: SchemeClaimDAO, authCodeDAO: AuthCodeDAO)(implicit exec: ExecutionContext) extends Controller {
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
      formWithErrors => Future.successful(Redirect(controllers.client.routes.ClientController.index())),
      empref => Future.successful(Redirect(controllers.gateway.routes.ClaimAuthController.auth(empref, "client1", controllers.client.routes.ClientController.claimCallback(None).url)))
    )
  }

  def claimCallback(code: Option[String]) = UserAction.async { implicit request =>
    val redirectToIndex = Redirect(controllers.client.routes.ClientController.index())

    code match {
      case None => Future.successful(Redirect(controllers.client.routes.ClientController.index()))
      case Some(c) => convertCode(c, request.user.id).flatMap {
        case Some(scr) => schemeClaimDAO.insert(scr).map { _ => redirectToIndex }
        case None => Future.successful(redirectToIndex)
      }
    }
  }

  case class AccessTokenResponse(access_token: String, expires_in: Long, scope: String, refreshToken: Option[String], token_type: String)

  object AccessTokenResponse {
    implicit val format = Json.format[AccessTokenResponse]
  }

  def convertCode(code: String, userId: Long)(implicit requestHeader: RequestHeader): Future[Option[SchemeClaimRow]] = {
    authCodeDAO.find(code).flatMap {
      case Some(authCode) => callAuthServer(userId, authCode).map(Some(_))
      case None => Future.successful(None)
    }
  }

  def callAuthServer(userId: Long, authCode: AuthCodeRow)(implicit rh: RequestHeader): Future[SchemeClaimRow] = {
    val params = Map(
      "grant_type" -> "authorization_code",
      "code" -> authCode.authorizationCode,
      "redirect_uri" -> "http://localhost:9000/",
      "client_id" -> authCode.clientId.get.toString,
      "client_secret" -> "secret1"
    ).map { case (k, v) => k -> Seq(v) }

    ws.url(controllers.routes.OAuth2Controller.accessToken().absoluteURL()).post(params).map { response =>
      response.status match {
        case 200 =>
          val r = response.json.validate[AccessTokenResponse].get
          val validUntil = DateTime.now.plus(r.expires_in * 1000)
          SchemeClaimRow(r.scope, userId, r.access_token, new Date(validUntil.getMillis), r.refreshToken)
      }
    }
  }
}

