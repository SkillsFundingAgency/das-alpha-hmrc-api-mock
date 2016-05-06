package uk.gov.bis.levyApiMock.playslicks

import javax.inject.Inject

import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.driver.JdbcProfile
import uk.gov.bis.levyApiMock.db.levy.EnrolmentModule

class Enrolments @Inject()(protected val dbConfigProvider: DatabaseConfigProvider) extends EnrolmentModule with HasDatabaseConfigProvider[JdbcProfile]
