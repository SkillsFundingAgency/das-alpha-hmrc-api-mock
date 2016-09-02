package uk.gov.bis.levyApiMock.actions

import com.google.inject.Inject
import uk.gov.bis.levyApiMock.data.oauth2.{AuthRecord, AuthRecordOps}

import scala.concurrent.{ExecutionContext, Future}

/**
  * An AuthenticatedAction checks that the access token is valid and provides the corresponding AuthRecord
  * to the action body
  */
class AuthenticatedAction @Inject()(authRecords: AuthRecordOps)(implicit val ec: ExecutionContext)
  extends AuthAction {

  override def validateToken(accessToken: String): Future[Option[AuthRecord]] = authRecords.find(accessToken)

}


