package uk.gov.bis.levyApiMock.playslicks

import javax.inject.Inject

import play.api.db.slick.DatabaseConfigProvider
import uk.gov.bis.levyApiMock.db.levy.LevyDeclarationModule

class LevyDeclarations @Inject()(protected val dbConfigProvider: DatabaseConfigProvider) extends LevyDeclarationModule
