package uk.gov.bis.levyApiMock.actions

import cats.data.OptionT
import cats.instances.future._
import com.google.inject.Inject
import uk.gov.bis.levyApiMock.auth.OAuthTrace
import uk.gov.bis.levyApiMock.data.oauth2.{AuthRecord, AuthRecordOps}
import uk.gov.bis.levyApiMock.data.{GatewayUserOps, TimeSource}

import scala.concurrent.{ExecutionContext, Future}

/**
  * an AuthorizedAction must both have a valid access token and that token must allow access to the given empref
  */
class AuthorizedAction @Inject()(authRecords: AuthRecordOps, users: GatewayUserOps, timeSource: TimeSource)(implicit executionContext: ExecutionContext) {
  def apply(empref: String): AuthAction = new AuthorizedActionBuilder(empref, authRecords, users, timeSource)
}

class AuthorizedActionBuilder(empref: String, authRecords: AuthRecordOps, users: GatewayUserOps, timeSource: TimeSource)(implicit val ec: ExecutionContext) extends AuthAction {
  override def validateToken(accessToken: String): Future[Option[AuthRecord]] = {
    OAuthTrace(s"looking for auth record for access token $accessToken")

    for {
      ar <- OptionT(authRecords.find(accessToken)) if !ar.accessTokenExpired(timeSource.currentTimeMillis())
      _ = OAuthTrace(s"found auth record $ar")
      hasAccess <- OptionT.liftF(checkAccess(ar)) if hasAccess
    } yield ar
  }.value

  private def checkAccess(ar: AuthRecord): Future[Boolean] = {
    if (ar.isPrivileged) Future.successful(true)
    else users.forGatewayID(ar.gatewayID).map(_.exists(_.empref == empref))
  }
}