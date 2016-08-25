package uk.gov.bis.levyApiMock.actions

import cats.data.OptionT
import cats.std.future._
import com.google.inject.Inject
import play.api.mvc.Results._
import play.api.mvc.{ActionBuilder, _}
import uk.gov.bis.levyApiMock.data.GatewayUserOps
import uk.gov.bis.levyApiMock.data.oauth2.{AuthRecord, AuthRecordOps}

import scala.concurrent.{ExecutionContext, Future}

case class AuthRequest[+A](authRecord: AuthRecord, request: Request[A]) extends WrappedRequest(request)

class AuthorizedActionBuilder(empref: String, authRecords: AuthRecordOps, users: GatewayUserOps)(implicit ec: ExecutionContext)
  extends ActionBuilder[AuthRequest] {
  override def invokeBlock[A](request: Request[A], next: (AuthRequest[A]) => Future[Result]): Future[Result] = {
    val BearerToken = "Bearer (.+)".r

    request.headers.get("Authorization") match {
      case Some(BearerToken(accessToken)) => validateToken(accessToken, empref).flatMap {
        case Some(authRecord) => next(AuthRequest(authRecord, request))
        case None => unauthorized("Bearer token does not grant access to the requested resource")
      }
      case Some(h) => unauthorized("Authorization header should be a Bearer token")
      case None => unauthorized("No Authorization header found")
    }
  }

  def validateToken[A](accessToken: String, empref: String)(implicit ec: ExecutionContext): Future[Option[AuthRecord]] = {
    for {
      ar <- OptionT(authRecords.find(accessToken))
      u <- OptionT(users.forGatewayID(ar.gatewayID))
    } yield ar
  }.value

  private def unauthorized(message: String): Future[Result] = Future.successful(Unauthorized(message))
}


class AuthorizedAction @Inject()(authRecords: AuthRecordOps, enrolments: GatewayUserOps)(implicit ec: ExecutionContext) {
  def apply(empref: String): AuthorizedActionBuilder =
    new AuthorizedActionBuilder(empref, authRecords, enrolments)(ec)


}
