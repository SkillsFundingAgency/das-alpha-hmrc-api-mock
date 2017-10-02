package uk.gov.bis.oauth.data

import uk.gov.bis.mongo.MongoDate

import scala.concurrent.{ExecutionContext, Future}

case class AuthCodeRow(authorizationCode: String, gatewayId: String, redirectUri: String, createdAt: MongoDate, scope: Option[String], clientId: Option[String], expiresIn: Int)

trait AuthCodeOps {
  def find(code: String)(implicit ec: ExecutionContext): Future[Option[AuthCodeRow]]

  def delete(code: String)(implicit ec: ExecutionContext): Future[Int]

  def create(code: String, gatewayUserId: String, redirectUri: String, clientId: String, scope: String)(implicit ec: ExecutionContext): Future[Int]
}
