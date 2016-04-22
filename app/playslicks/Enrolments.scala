package playslicks

import javax.inject.Inject

import db.levy.EnrolmentModule
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.driver.JdbcProfile

class Enrolments @Inject()(protected val dbConfigProvider: DatabaseConfigProvider) extends EnrolmentModule with HasDatabaseConfigProvider[JdbcProfile]
