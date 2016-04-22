package playslicks

import javax.inject.Inject

import db.levy.LevyDeclarationModule
import play.api.db.slick.DatabaseConfigProvider

class LevyDeclarations @Inject()(protected val dbConfigProvider: DatabaseConfigProvider) extends LevyDeclarationModule
