package db

import play.api.db.slick.DatabaseConfigProvider

trait DBModule {
  def dbConfigProvider: DatabaseConfigProvider
}
