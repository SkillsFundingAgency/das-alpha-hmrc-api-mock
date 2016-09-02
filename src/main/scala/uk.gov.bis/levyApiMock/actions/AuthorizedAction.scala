package uk.gov.bis.levyApiMock.actions

import cats.data.OptionT
import com.google.inject.Inject
import uk.gov.bis.levyApiMock.data.GatewayUserOps
import uk.gov.bis.levyApiMock.data.oauth2.{AuthRecord, AuthRecordOps}

import scala.concurrent.{ExecutionContext, Future}

class AuthorizedAction @Inject()(authRecords: AuthRecordOps, users: GatewayUserOps)(implicit executionContext: ExecutionContext) {
  def apply(empref: String): AuthAction = new AuthorizedActionBuilder(empref, authRecords, users)
}

class AuthorizedActionBuilder(empref: String, authRecords: AuthRecordOps, users: GatewayUserOps)(implicit val ec: ExecutionContext) extends AuthAction {
  override def validateToken(accessToken: String): Future[Option[AuthRecord]] = {
    import cats.instances.future._
    for {
      ar <- OptionT(authRecords.find(accessToken))
      u <- OptionT(users.forGatewayID(ar.gatewayID))
    } yield ar
  }.value
}