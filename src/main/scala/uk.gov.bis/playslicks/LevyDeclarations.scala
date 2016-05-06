package uk.gov.bis.playslicks

import javax.inject.Inject

import uk.gov.bis.db.levy.LevyDeclarationModule
import play.api.db.slick.DatabaseConfigProvider

class LevyDeclarations @Inject()(protected val dbConfigProvider: DatabaseConfigProvider) extends LevyDeclarationModule
