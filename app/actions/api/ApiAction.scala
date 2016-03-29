package actions.api

import javax.inject.Singleton

import com.google.inject.Inject
import db.outh2.AccessTokenDAO
import play.api.Logger
import play.api.mvc.Results._
import play.api.mvc._

import scala.concurrent.{ExecutionContext, Future}

class ApiRequest[A](val request: Request[A], val emprefs: List[String]) extends WrappedRequest[A](request)

@Singleton
class ApiAction @Inject()(accessTokens: AccessTokenDAO)(implicit ec: ExecutionContext)
  extends ActionBuilder[ApiRequest]
    with ActionRefiner[Request, ApiRequest] {

  override protected def refine[A](request: Request[A]): Future[Either[Result, ApiRequest[A]]] = {
    implicit val rh: RequestHeader = request

    val BearerToken = "Bearer (.+)".r
    request.headers.get("Authorization") match {
      case Some(BearerToken(accessToken)) => accessTokens.find(accessToken).map {
        case Some(at) =>
          Logger.info(s"Found access token with scope ${at.scope}")
          Right(new ApiRequest(request, List(at.scope)))
        case _ =>
          Logger.info(s"No authorization found for Bearer $accessToken")
          Left(Unauthorized(s"No authorization found for Bearer $accessToken"))
      }
      case _ =>
        Logger.info("No Authorization header found")
        Future.successful(Left(Unauthorized("No Authorization header found")))
    }
  }
}
