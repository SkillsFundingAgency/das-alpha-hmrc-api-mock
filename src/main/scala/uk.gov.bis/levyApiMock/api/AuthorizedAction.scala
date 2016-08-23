package uk.gov.bis.levyApiMock.api

import com.google.inject.Inject
import uk.gov.bis.levyApiMock.data.levy.GatewayUserOps
import uk.gov.bis.levyApiMock.data.oauth2.{AuthRecord, AuthRecordOps}
import play.api.mvc.Results._
import play.api.mvc.{ActionBuilder, _}

import scala.concurrent.{ExecutionContext, Future}

case class AuthRequest[+A](authRecord: AuthRecord, request: Request[A]) extends WrappedRequest(request)

class AuthorizedActionBuilder(identifierType: String, taxId: String, scope: String, authRecords: AuthRecordOps, enrolments: GatewayUserOps)(implicit ec: ExecutionContext)
  extends ActionBuilder[AuthRequest] {
  override def invokeBlock[A](request: Request[A], next: (AuthRequest[A]) => Future[Result]): Future[Result] = {
    val BearerToken = "Bearer (.+)".r

    request.headers.get("Authorization") match {
      case Some(BearerToken(accessToken)) => validateToken(accessToken, identifierType, taxId, scope).flatMap {
        case Some(authRecord) => next(AuthRequest(authRecord, request))
        case None => unauthorized("Bearer token does not grant access to the requested resource")
      }
      case Some(h) => unauthorized("Authorization header should be a Bearer token")
      case None => unauthorized("No Authorization header found")
    }
  }

  def validateToken[A](accessToken: String, identifierType: String, taxId: String, scope: String): Future[Option[AuthRecord]] =
    authRecords.find(accessToken, identifierType, taxId, scope)

  private def unauthorized(message: String): Future[Result] = Future.successful(Unauthorized(message))
}


class AuthorizedAction @Inject()(authRecords: AuthRecordOps, enrolments: GatewayUserOps)(implicit ec: ExecutionContext) {
  def apply(identifierType: String, taxId: String, scope: String): AuthorizedActionBuilder =
    new AuthorizedActionBuilder(identifierType, taxId, scope, authRecords, enrolments)(ec)
}
