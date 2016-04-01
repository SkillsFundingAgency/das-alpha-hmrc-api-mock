package db

import play.api.db.slick.HasDatabaseConfigProvider
import slick.driver.JdbcProfile

import scala.concurrent.{ExecutionContext, Future}

trait DBModule extends HasDatabaseConfigProvider[JdbcProfile] {
  implicit def ec: ExecutionContext

  import driver.api._

  def run[R, S <: NoStream, E <: Effect](action: DBIOAction[R, S, E]): Future[R] = db.run(action)
}
