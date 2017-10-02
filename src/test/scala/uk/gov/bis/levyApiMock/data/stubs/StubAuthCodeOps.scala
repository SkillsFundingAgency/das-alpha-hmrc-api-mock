package uk.gov.bis.levyApiMock.data.stubs

import uk.gov.bis.oauth.data.{AuthCodeOps, AuthCodeRow}

import scala.concurrent.{ExecutionContext, Future}

trait StubAuthCodeOps extends AuthCodeOps{
  override def find(code: String)(implicit ec: ExecutionContext): Future[Option[AuthCodeRow]] = ???

  override def delete(code: String)(implicit ec: ExecutionContext): Future[Int] = ???

  override def create(code: String, gatewayUserId: String, redirectUri: String, clientId: String, scope: String)(implicit ec: ExecutionContext): Future[Int] = ???
}
