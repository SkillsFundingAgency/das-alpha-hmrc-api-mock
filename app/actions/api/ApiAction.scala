package actions.api

import com.google.inject.Inject
import db.levy.GatewayIdSchemeOps
import db.outh2.AuthRecordOps
import play.api.mvc.Results._
import play.api.mvc._

import scala.concurrent.{ExecutionContext, Future}

class ApiRequest[A](val request: Request[A], val emprefs: List[String]) extends WrappedRequest[A](request)

class ApiAction @Inject()(authRecords: AuthRecordOps, enrolments: GatewayIdSchemeOps)(implicit ec: ExecutionContext)
  extends ActionBuilder[ApiRequest]
    with ActionRefiner[Request, ApiRequest] {

  private def unauthorized(message: String): Future[Left[Result, Nothing]] = Future.successful(Left(Unauthorized(message)))

  override protected[api] def refine[A](request: Request[A]): Future[Either[Result, ApiRequest[A]]] = {
    val BearerToken = "Bearer (.+)".r

    request.headers.get("Authorization") match {
      case Some(BearerToken(accessToken)) => validateToken(accessToken).map {
        case Right(emprefs) => Right(new ApiRequest[A](request, emprefs))
        case Left(message) => Left(Unauthorized(message))
      }
      case Some(h) => unauthorized("Authorization header should be a Bearer token")
      case None => unauthorized("No Authorization header found")
    }
  }

  def validateToken[A](accessToken: String): Future[Either[String, List[String]]] = {
    authRecords.find(accessToken).flatMap {
      case Some(at) => enrolments.emprefsForId(at.gatewayId).map(emprefs => Right(emprefs.toList))
      case _ => Future.successful(Left(s"No authorization found for Bearer $accessToken"))
    }
  }
}
