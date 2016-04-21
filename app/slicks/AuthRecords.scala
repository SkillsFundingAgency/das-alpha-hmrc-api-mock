package slicks

import javax.inject.Inject

import db.oauth2.AuthRecordModule
import play.api.db.slick.DatabaseConfigProvider

class AuthRecords @Inject()(protected val dbConfigProvider: DatabaseConfigProvider) extends AuthRecordModule
