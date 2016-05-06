package uk.gov.bis.levyApiMock.playslicks

import javax.inject.Inject

import play.api.db.slick.DatabaseConfigProvider
import uk.gov.bis.levyApiMock.db.oauth2.AuthRecordModule

class AuthRecords @Inject()(protected val dbConfigProvider: DatabaseConfigProvider) extends AuthRecordModule
