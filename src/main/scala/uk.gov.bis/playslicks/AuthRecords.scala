package uk.gov.bis.playslicks

import javax.inject.Inject

import uk.gov.bis.db.oauth2.AuthRecordModule
import play.api.db.slick.DatabaseConfigProvider

class AuthRecords @Inject()(protected val dbConfigProvider: DatabaseConfigProvider) extends AuthRecordModule
