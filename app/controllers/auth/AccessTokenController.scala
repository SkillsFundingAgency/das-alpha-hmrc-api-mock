package controllers.auth

import javax.inject.Inject

import data.levy.{EnrolmentOps, ServiceBinding}
import data.oauth2.{AuthRecord, AuthRecordOps}
import play.api.libs.json.Json
import play.api.mvc.{Action, Controller}

import scala.concurrent.{ExecutionContext, Future}

class AccessTokenController @Inject()(authRecords: AuthRecordOps, enrolments: EnrolmentOps)(implicit ec: ExecutionContext) extends Controller {

  import AccessTokenController.processScopes

  case class Token(value: String, scopes: List[String], gatewayId: String, enrolments: List[ServiceBinding], clientId: String, expiresAt: Long)

  implicit val sbFormat = Json.format[ServiceBinding]
  implicit val tokenFormat = Json.format[Token]

  /**
    * Accept a json structure that describes an access token and the information it relates to, including
    * the list of emprefs that the token grants access to
    */
  def provideToken = Action.async(parse.json) { implicit request =>
    request.body.validate[Token].map { token =>
      val ats = processScopes(token.scopes).map { scope => AuthRecord(token.value, scope, token.gatewayId, token.clientId, token.expiresAt, System.currentTimeMillis()) }

      // lear out any expired tokens in the background and ignore any db
      // errors that might occur
      authRecords.clearExpired().recover { case _ => () }

      // Independent operations - run concurrently
      Future.sequence(Seq(
        authRecords.create(ats),
        enrolments.bindEnrolments(token.gatewayId, token.enrolments)
      )).map(_ => NoContent)
    }.getOrElse(Future(BadRequest))
  }


}

object AccessTokenController {
  val opendIdConnectScopes = List("profile", "enrolments")

  /**
    * Check for the "openid" scope and, if present, convert any valid OpenID Connect scope values.
    * For example "profile" would become "openid:profile"
    */
  def processScopes(scopes: List[String]): List[String] = {
    if (scopes.contains("openid")) {
      scopes.filter(_ != "openid").map(s => if (opendIdConnectScopes.contains(s)) s"openid:$s" else s)
    } else scopes
  }
}
