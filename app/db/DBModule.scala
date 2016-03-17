package db

import play.api.db.slick.HasDatabaseConfigProvider
import slick.driver.JdbcProfile

trait DBModule extends HasDatabaseConfigProvider[JdbcProfile]
