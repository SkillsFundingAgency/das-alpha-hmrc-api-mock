package uk.gov.bis.oauth.auth

import uk.gov.bis.oauth.data.ClientOps

import scala.concurrent.{ExecutionContext, Future}
import scalaoauth2.provider.AuthorizationRequest

trait ValidateClientHandler {
  def applications: ClientOps

  implicit def ec: ExecutionContext

  def validateClient(request: AuthorizationRequest): Future[Boolean] = {
    OAuthTrace("validate client")
    request.clientCredential match {
      case Some(cred) =>
        OAuthTrace(cred.toString)
        applications.validate(cred.clientId, cred.clientSecret, request.grantType)
      case None => Future.successful(false)
    }
  }
}
