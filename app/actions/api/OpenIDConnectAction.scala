package actions.api

import com.google.inject.Inject
import data.oauth2.AuthRecordOps
import play.api.mvc.Results._
import play.api.mvc._

import scala.concurrent.{ExecutionContext, Future}

class OpenIDConnectRequest[A](val request: Request[A], val token: String, val claims: List[String]) extends WrappedRequest[A](request)

class OpenIDConnectActionRefiner(authRecords: AuthRecordOps)(implicit ec: ExecutionContext)
  extends ActionBuilder[OpenIDConnectRequest] with ActionRefiner[Request, OpenIDConnectRequest] {

  override protected def refine[A](request: Request[A]): Future[Either[Result, OpenIDConnectRequest[A]]] = {
    val BearerToken = "Bearer (.+)".r

    request.headers.get("Authorization") match {
      case Some(BearerToken(accessToken)) =>
        for {
          scopes <- authRecords.scopes(accessToken)
        } yield Right(new OpenIDConnectRequest(request, accessToken, scopes.toList))

      case Some(h) => unauthorized("Authorization header should be a Bearer token")
      case None => unauthorized("No Authorization header found")
    }
  }

  private def unauthorized(message: String) = Future.successful(Left(Unauthorized(message)))
}


class OpenIDConnectAction @Inject()(authRecords: AuthRecordOps)(implicit ec: ExecutionContext) {
  def apply(): OpenIDConnectActionRefiner = new OpenIDConnectActionRefiner(authRecords)(ec)
}
