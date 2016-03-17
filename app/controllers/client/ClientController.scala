package controllers.client

import java.sql.Date
import javax.inject.Inject

import actions.client.ClientUserAction
import cats.data.OptionT
import cats.std.future._
import db.client.{SchemeClaimRow, DASUserDAO, SchemeClaimDAO, SchemeDAO}
import db.outh2.AuthCodeDAO
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
    code match {
      case None => Future.successful(Redirect(controllers.client.routes.ClientController.index()))
      case Some(c) => convertCode(c, request.user.id).flatMap { case Some(scr) =>
        schemeClaimDAO.insert(scr).map { _ =>
          Redirect(controllers.client.routes.ClientController.index())
        }
      }
    }
  }

  case class AccessTokenResponse(access_token: String, expires_in: Long, scope: Option[String], refreshToken: Option[String], token_type: String)

  object AccessTokenResponse {
    implicit val format = Json.format[AccessTokenResponse]
  }

  def convertCode(code: String, userId: Long)(implicit requestHeader: RequestHeader): Future[Option[SchemeClaimRow]] = {
    val f = authCodeDAO.find(code).map { ac =>
      ac map { authCode =>
        val params = Map(
          "grant_type" -> "authorization_code",
          "code" -> authCode.authorizationCode,
          "redirect_uri" -> "http://localhost:9000/",
          "client_id" -> authCode.clientId.get.toString,
          "client_secret" -> "secret1"
        ).map{case (k,v) => k -> Seq(v)}

        callAuthServer(userId, params)
      }
    }
    f.flatMap {
      case Some(f2) => f2.map(Some(_))
      case None => Future.successful(None)
    }
  }

  def callAuthServer(userId: Long, params: Map[String, Seq[String]])(implicit rh: RequestHeader): Future[SchemeClaimRow] = {
    val url: String = controllers.routes.OAuth2Controller.accessToken().absoluteURL()
    println(url)
    println(params)
    ws.url(url).post(params).map { response =>
      response.status match {
        case 200 =>
          val r = response.json.validate[AccessTokenResponse].get
          val validUntil = DateTime.now.plus(r.expires_in * 1000)
          SchemeClaimRow(r.scope.get, userId, r.access_token, new Date(validUntil.getMillis), r.refreshToken)
      }
    }
  }
}

