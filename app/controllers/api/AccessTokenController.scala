package controllers.api

import java.util.Date
import javax.inject.{Inject, Singleton}

import db.outh2.{AccessTokenDAO, AccessTokenRow}
import play.api.libs.json.{JsError, JsSuccess, Json}
import play.api.mvc.{Action, Controller}

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class AccessTokenController @Inject()(accessTokens: AccessTokenDAO)(implicit ec: ExecutionContext) extends Controller {

  // Caller must supply this
  val securityKey = "abcdefg"

  case class Token(value: String, scope: String, expiresAt: Date)
  implicit val tokenFormat = Json.format[Token]

  def provideToken = Action.async(parse.json) { implicit request =>
    request.body.validate[Token] match {
      case JsSuccess(token, _) =>
        val at = AccessTokenRow(token.value, token.scope, new java.sql.Date(token.expiresAt.getTime), new java.sql.Date(System.currentTimeMillis()))
        accessTokens.deleteExistingAndCreate(at).map(_ => NoContent)

      case JsError(_) => Future.successful(BadRequest)
    }
  }

}
