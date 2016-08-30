package uk.gov.bis.levyApiMock.data

import scala.concurrent.{ExecutionContext, Future}

case class Scope(name: String, description: String)

trait ScopeOps {
  def byName(name: String)(implicit ec: ExecutionContext): Future[Option[Scope]]
}
