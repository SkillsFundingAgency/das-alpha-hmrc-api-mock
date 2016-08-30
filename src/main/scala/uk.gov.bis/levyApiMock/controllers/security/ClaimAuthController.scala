package uk.gov.bis.levyApiMock.controllers.security

import javax.inject.{Inject, Singleton}

import cats.data.Xor.{Left, Right}
import cats.data.{Xor, XorT}
import cats.instances.future._
import cats.syntax.xor._
import play.api.mvc.{Action, Controller}
import uk.gov.bis.levyApiMock.data._

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class ClaimAuthController @Inject()(scopes: ScopeOps, authCodes: AuthCodeOps, authIds: AuthIdOps, clients: ClientOps)(implicit ec: ExecutionContext) extends Controller {

  def orError[A](ao: Option[A], err: String): Xor[String, A] = ao.fold[Xor[String, A]](err.left)(a => a.right)

  /**
    * Handle the initial oAuth request
    */
  def authorize(scopeName: String, clientId: String, redirectUri: String, state: Option[String]) = Action.async {
    implicit request =>
      val authIdOrError = for {
        _ <- XorT(clients.forId(clientId).map(c => orError(c, "unknown client id")))
        _ <- XorT(scopes.byName(scopeName).map(s => orError(s, "unknown scope")))
      } yield AuthId(scopeName, clientId, redirectUri, state)


      authIdOrError.value.flatMap {
        case Left(err) => Future.successful(BadRequest(err))
        case Right(a) => authIds.stash(a).map(id => Redirect(routes.GrantScopeController.show(id)))
      }
  }
}