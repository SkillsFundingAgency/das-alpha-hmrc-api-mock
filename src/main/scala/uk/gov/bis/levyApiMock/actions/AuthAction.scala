package uk.gov.bis.levyApiMock.actions

import play.api.mvc.Results._
import play.api.mvc.{ActionBuilder, Request, Result, WrappedRequest}
import uk.gov.bis.levyApiMock.data.oauth2.AuthRecord

import scala.concurrent.{ExecutionContext, Future}

case class AuthRequest[+A](authRecord: AuthRecord, request: Request[A]) extends WrappedRequest(request)

trait AuthAction extends ActionBuilder[AuthRequest] {
  implicit val ec: ExecutionContext

  override def invokeBlock[A](request: Request[A], next: (AuthRequest[A]) => Future[Result]): Future[Result] = {
    val BearerToken = "Bearer (.+)".r

    request.headers.get("Authorization") match {
      case Some(BearerToken(accessToken)) => validateToken(accessToken).flatMap {
        case Some(authRecord) => next(AuthRequest(authRecord, request))
        case None => unauthorized("Bearer token does not grant access to the requested resource")
      }
      case Some(h) => unauthorized("Authorization header should be a Bearer token")
      case None => unauthorized("No Authorization header found")
    }
  }

  def validateToken(accessToken: String): Future[Option[AuthRecord]]

  private def unauthorized(message: String): Future[Result] = Future.successful(Unauthorized(message))
}
