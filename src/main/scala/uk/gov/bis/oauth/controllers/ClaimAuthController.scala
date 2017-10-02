package uk.gov.bis.oauth.controllers

import javax.inject.{Inject, Singleton}

import cats.data.EitherT
import cats.instances.future._
import cats.syntax.either._
import play.api.data.Form
import play.api.data.Forms._
import play.api.mvc.{Action, Controller}
import uk.gov.bis.mongo.MongoDate
import uk.gov.bis.oauth.data._
import uk.gov.bis.utils.TimeSource

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class ClaimAuthController @Inject()(
  scopeOps: ScopeOps,
  authIds: AuthRequestOps,
  clients: ClientOps,
  timeSource: TimeSource
)(implicit ec: ExecutionContext)
  extends Controller {

  implicit class ErrorSyntax[A](ao: Option[A]) {
    def orError(err: String): Either[String, A] =
      ao.fold[Either[String, A]](Left(err))(a => Right(a))
  }

  /**
    * Handle the initial oAuth request
    */
  def authorize(
    scope: String,
    clientId: String,
    redirectUri: String,
    state: Option[String]
  ) = Action.async { implicit request =>
    val scopes = scope.split(" ").toList.filter(_ != "")
    handleAuth(scopes, clientId, redirectUri, state)
  }

  private def handleAuth(
    scopes: Seq[String],
    clientId: String,
    redirectUri: String,
    state: Option[String]
  ) = {
    val clientCheck = clients.forId(clientId).map(_.orError("unknown client id"))
    val scopeCheck: Future[Either[String, Seq[Scope]]] = scopeOps.byNames(scopes).map {
      _.leftMap(badScopes => s"Unknown scopes: ${badScopes.mkString(" , ")}")
    }

    val authIdOrError = for {
      _ <- EitherT(clientCheck)
      _ <- EitherT(scopeCheck)
    } yield
      AuthRequest(
        scopes.mkString(" "),
        clientId,
        redirectUri,
        state,
        0,
        MongoDate.fromLong(timeSource.currentTimeMillis()))

    authIdOrError.value.flatMap {
      case Left(err) => Future.successful(BadRequest(err))
      case Right(a)  =>
        authIds
          .stash(a)
          .map(id => Redirect(routes.GrantScopeController.show(id)))
    }
  }

  def authorizePost = Action.async { implicit request =>
    case class Params(
      scopeName: String,
      clientId: String,
      redirectUri: String,
      state: Option[String]
    )
    val m = mapping(
      "scopeName" -> text,
      "clientId" -> text,
      "redirectUrl" -> text,
      "state" -> optional(text)
    )(Params.apply)(Params.unapply)

    Form(m)
      .bindFromRequest()
      .fold(
        errs => Future.successful(BadRequest(errs.toString)),
        params =>
          handleAuth(
            params.scopeName.split(" ").filter(_ != ""),
            params.clientId,
            params.redirectUri,
            params.state)
      )
  }
}
