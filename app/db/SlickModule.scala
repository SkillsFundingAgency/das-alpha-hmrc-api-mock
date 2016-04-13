package db

import play.api.db.slick.HasDatabaseConfigProvider
import slick.driver.JdbcProfile

import scala.concurrent.{ExecutionContext, Future}

trait SlickModule extends HasDatabaseConfigProvider[JdbcProfile] {
  implicit def ec: ExecutionContext

  val api =  driver.api
  import api._

  def run[R, S <: NoStream, E <: Effect](action: DBIOAction[R, S, E]): Future[R] = db.run(action)
}
