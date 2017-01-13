package uk.gov.bis.levyApiMock.controllers.security

import uk.gov.bis.levyApiMock.data.{Application, ClientOps, TimeSource}

import scala.concurrent.{ExecutionContext, Future}

trait StubClientOps extends ClientOps {
  override def timeSource: TimeSource = ???

  override def forId(clientID: String)(implicit ec: ExecutionContext): Future[Option[Application]] = ???
}
