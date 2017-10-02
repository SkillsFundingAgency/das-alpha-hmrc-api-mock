package uk.gov.bis.levyApiMock.data.stubs

import uk.gov.bis.oauth.data.{Application, ClientOps}
import uk.gov.bis.utils.TimeSource

import scala.concurrent.{ExecutionContext, Future}

trait StubClientOps extends ClientOps {
  override def timeSource: TimeSource = ???

  override def forId(clientID: String)(implicit ec: ExecutionContext): Future[Option[Application]] = ???
}
