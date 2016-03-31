package controllers.api

import javax.inject.{Inject, Singleton}

import db.levy.GatewayIdSchemeDAO
import db.outh2.{AccessTokenDAO, AccessTokenRow}
import play.api.Logger
import play.api.libs.json.{JsError, JsSuccess, Json}
import play.api.mvc.{Action, Controller}

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class AccessTokenController @Inject()(accessTokens: AccessTokenDAO, enrolments: GatewayIdSchemeDAO)(implicit ec: ExecutionContext) extends Controller {

  case class Token(value: String, scope: String, gatewayId: String, emprefs: List[String], clientId: String, expiresAt: Long)

  implicit val tokenFormat = Json.format[Token]

  def provideToken = Action.async(parse.json) { implicit request =>
    request.body.validate[Token] match {
      case JsSuccess(token, _) =>
        val at = AccessTokenRow(token.value, token.scope, token.gatewayId, token.clientId, token.expiresAt, System.currentTimeMillis())
        Logger.info(s"new token received for scope ${token.scope}")
        for {
          _ <- accessTokens.create(at)
          _ <- enrolments.bindEmprefs(token.gatewayId, token.emprefs)
        } yield NoContent


      case JsError(_) => Future.successful(BadRequest)
    }
  }

}
