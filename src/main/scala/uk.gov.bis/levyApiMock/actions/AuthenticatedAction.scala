package uk.gov.bis.levyApiMock.actions

import com.google.inject.Inject
import play.api.mvc.Results._
import play.api.mvc.{ActionBuilder, _}
import uk.gov.bis.levyApiMock.data.GatewayUserOps
import uk.gov.bis.levyApiMock.data.oauth2.{AuthRecord, AuthRecordOps}

import scala.concurrent.{ExecutionContext, Future}

class AuthenticatedActionBuilder(authRecords: AuthRecordOps)(implicit ec: ExecutionContext)
  extends ActionBuilder[AuthRequest] {
  override def invokeBlock[A](request: Request[A], next: (AuthRequest[A]) => Future[Result]): Future[Result] = {
    val BearerToken = "Bearer (.+)".r

    request.headers.get("Authorization") match {
      case Some(BearerToken(accessToken)) => validateToken(accessToken).flatMap {
        case Some(authRecord) => next(AuthRequest(authRecord, request))
        case None => unauthorized(s"Bearer token ($accessToken) is not valid")
      }
      case Some(h) => unauthorized("Authorization header should be a Bearer token")
      case None => unauthorized("No Authorization header found")
    }
  }

  def validateToken[A](accessToken: String): Future[Option[AuthRecord]] = authRecords.find(accessToken)

  private def unauthorized(message: String): Future[Result] = Future.successful(Unauthorized(message))
}


class AuthenticatedAction @Inject()(authRecords: AuthRecordOps, enrolments: GatewayUserOps)(implicit ec: ExecutionContext) {
  def apply(): AuthenticatedActionBuilder = new AuthenticatedActionBuilder(authRecords)(ec)
}
