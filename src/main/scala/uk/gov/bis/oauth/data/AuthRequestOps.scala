package uk.gov.bis.oauth.data

import uk.gov.bis.mongo.MongoDate

import scala.concurrent.{ExecutionContext, Future}

case class AuthRequest(scope: String, clientId: String, redirectUri: String, state: Option[String], id: Long, creationDate: MongoDate)

trait AuthRequestOps {
  /**
    * @return a generated identifier for the authRequest record
    */
  def stash(authRequest: AuthRequest)(implicit ec: ExecutionContext): Future[Long]

  def pop(id: Long)(implicit ec: ExecutionContext): Future[Option[AuthRequest]]
}
