package controllers.api

import javax.inject.{Inject, Singleton}

import db.levy.GatewayIdSchemeOps
import db.outh2.{AuthRecordOps, AuthRecordRow}
import play.api.libs.json.{JsError, JsSuccess, Json}
import play.api.mvc.{Action, Controller}

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class AccessTokenController @Inject()(authRecords: AuthRecordOps, enrolments: GatewayIdSchemeOps)(implicit ec: ExecutionContext) extends Controller {

  case class Token(value: String, scope: String, gatewayId: String, emprefs: List[String], clientId: String, expiresAt: Long)

  implicit val tokenFormat = Json.format[Token]

  def provideToken = Action.async(parse.json) { implicit request =>
    request.body.validate[Token] match {
      case JsError(_) => Future.successful(BadRequest)

      case JsSuccess(token, _) =>
        val at = AuthRecordRow(token.value, token.scope, token.gatewayId, token.clientId, token.expiresAt, System.currentTimeMillis())

        // lear out any expired tokens in the background and ignore any db conflicts that
        // might occurs
        authRecords.clearExpired().recover { case _ => () }

        // Independent operations - run concurrently
        val c = authRecords.create(at)
        val e = enrolments.bindEmprefs(token.gatewayId, token.emprefs)

        for {
          _ <- c
          _ <- e
        } yield NoContent
    }
  }

}
