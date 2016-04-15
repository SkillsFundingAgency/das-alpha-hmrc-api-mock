package actions.api

import com.google.inject.Inject
import data.levy.EnrolmentOps
import data.oauth2.AuthRecordOps
import play.api.mvc.Results._
import play.api.mvc.{ActionBuilder, _}

import scala.concurrent.{ExecutionContext, Future}

class AuthorizedRequest[A](val request: Request[A], val emprefs: List[String]) extends WrappedRequest[A](request)

class AuthorizedActionBuilder(identifierType: String, taxId: String, scope: String, authRecords: AuthRecordOps, enrolments: EnrolmentOps)(implicit ec: ExecutionContext)
  extends ActionBuilder[Request] {
  override def invokeBlock[A](request: Request[A], block: (Request[A]) => Future[Result]): Future[Result] = {
    val BearerToken = "Bearer (.+)".r

    request.headers.get("Authorization") match {
      case Some(BearerToken(accessToken)) => validateToken(accessToken, identifierType, taxId, scope).flatMap {
        case true => block(request)
        case false => unauthorized("Bearer token does not grant access to the requested resource")
      }
      case Some(h) => unauthorized("Authorization header should be a Bearer token")
      case None => unauthorized("No Authorization header found")
    }
  }

  def validateToken[A](accessToken: String, identifierType: String, taxId: String, scope: String): Future[Boolean] =
    authRecords.find(accessToken, identifierType, taxId, scope).map(_.isDefined)

  private def unauthorized(message: String): Future[Result] = Future.successful(Unauthorized(message))
}


class AuthorizedAction @Inject()(authRecords: AuthRecordOps, enrolments: EnrolmentOps)(implicit ec: ExecutionContext) {
  def apply[A](identifierType: String, taxId: String, scope: String): AuthorizedActionBuilder =
    new AuthorizedActionBuilder(identifierType, taxId, scope, authRecords, enrolments)(ec)
}
