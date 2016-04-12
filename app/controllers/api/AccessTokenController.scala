package controllers.api

import javax.inject.{Inject, Singleton}

import data.levy.GatewayIdSchemeOps
import data.oauth2.{AuthRecordOps, AuthRecord}
import play.api.libs.json.{JsError, JsSuccess, Json}
import play.api.mvc.{Action, Controller}

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class AccessTokenController @Inject()(authRecords: AuthRecordOps, enrolments: GatewayIdSchemeOps)(implicit ec: ExecutionContext) extends Controller {

  case class Token(value: String, scope: String, gatewayId: String, emprefs: List[String], clientId: String, expiresAt: Long)

  implicit val tokenFormat = Json.format[Token]

  def provideToken = Action.async(parse.json) { implicit request =>
    request.body.validate[Token].map { token =>
      val at = AuthRecord(token.value, token.scope, token.gatewayId, token.clientId, token.expiresAt, System.currentTimeMillis())

      // lear out any expired tokens in the background and ignore any db
      // errors that might occur
      authRecords.clearExpired().recover { case _ => () }

      // Independent operations - run concurrently
      Future.sequence(Seq(
        authRecords.create(at),
        enrolments.bindEmprefs(token.gatewayId, token.emprefs)
      )).map(_ => NoContent)
    }.getOrElse(Future(BadRequest))
  }

}
