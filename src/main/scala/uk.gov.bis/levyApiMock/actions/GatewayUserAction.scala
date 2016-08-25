package uk.gov.bis.levyApiMock.actions

import com.google.inject.Inject
import play.api.mvc.Results._
import play.api.mvc._
import uk.gov.bis.levyApiMock.data.{GatewayUser, GatewayUserOps}
import uk.gov.bis.levyApiMock.TaxServiceConfig.config

import scala.concurrent.{ExecutionContext, Future}

class GatewayUserRequest[A](val request: Request[A], val user: GatewayUser) extends WrappedRequest[A](request)

class GatewayUserAction @Inject()(gatewayUsers: GatewayUserOps)(implicit ec: ExecutionContext)
  extends ActionBuilder[GatewayUserRequest]
    with ActionRefiner[Request, GatewayUserRequest] {

  val sessionKey = "mtdpId"
  val validatedUserKey = "validatedUser"

  val continueKey = "continue"

  override protected def refine[A](request: Request[A]): Future[Either[Result, GatewayUserRequest[A]]] = {
    implicit val rh: RequestHeader = request
    val uri = config.baseURI + "/gateway/sign-in"
    val redirectToSignIn = Left(Redirect("").addingToSession(continueKey -> request.uri))

    request.session.get(sessionKey) match {
      case None => Future.successful(redirectToSignIn)
      case Some(id) => gatewayUsers.forGatewayID(id).map {
        case Some(u) => Right(new GatewayUserRequest(request, u))
        case None => redirectToSignIn
      }
    }
  }
}

