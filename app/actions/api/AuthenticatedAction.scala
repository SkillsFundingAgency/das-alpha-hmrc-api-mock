package actions.api

import com.google.inject.Inject
import db.levy.GatewayIdSchemeOps
import db.outh2.AuthRecordOps
import play.api.mvc.Results._
import play.api.mvc.{ActionBuilder, _}

import scala.concurrent.{ExecutionContext, Future}

class AuthenticatedRequest[A](val request: Request[A], val emprefs: List[String]) extends WrappedRequest[A](request)

class AuthenticatedActionBuilder(taxId: String, scope: String, authRecords: AuthRecordOps, enrolments: GatewayIdSchemeOps)(implicit ec: ExecutionContext)
  extends ActionBuilder[Request] {
  override def invokeBlock[A](request: Request[A], block: (Request[A]) => Future[Result]): Future[Result] = {
    val BearerToken = "Bearer (.+)".r

    request.headers.get("Authorization") match {
      case Some(BearerToken(accessToken)) => validateToken(accessToken, taxId, scope).flatMap {
        case Right(true) => block(request)
        case Right(false) => unauthorized("Bearer token does not grant access to the requested resource")
        case Left(message) => unauthorized(message)
      }
      case Some(h) => unauthorized("Authorization header should be a Bearer token")
      case None => unauthorized("No Authorization header found")
    }
  }

  def validateToken[A](accessToken: String, taxId: String, scope: String): Future[Either[String, Boolean]] = {
    authRecords.find(accessToken, taxId, scope).flatMap {
      case Some(at) => enrolments.emprefsForId(at.gatewayId).map(emprefs => Right(emprefs.contains(taxId)))
      case _ => Future.successful(Left(s"No authorization found for Bearer $accessToken"))
    }
  }

  private def unauthorized(message: String): Future[Result] = Future.successful(Unauthorized(message))
}


class AuthenticatedAction @Inject()(authRecords: AuthRecordOps, enrolments: GatewayIdSchemeOps)(implicit ec: ExecutionContext) {

  def apply[A](taxId: String, scope: String): ActionBuilder[Request] = new AuthenticatedActionBuilder(taxId, scope, authRecords, enrolments)(ec)

}
