import com.google.inject.AbstractModule
import data.levy.{EnrolmentOps, LevyDeclarationOps}
import data.oauth2.AuthRecordOps
import db.levy.{EnrolmentDAO, EnrolmentModule, LevyDeclarationDAO, LevyDeclarationModule}
import db.oauth2.{AuthRecordDAO, AuthRecordModule}
import playslicks.{AuthRecords, Enrolments, LevyDeclarations}

class Module extends AbstractModule {

  override def configure() = {
    bind(classOf[EnrolmentOps]).to(classOf[EnrolmentDAO])
    bind(classOf[LevyDeclarationOps]).to(classOf[LevyDeclarationDAO])
    bind(classOf[AuthRecordOps]).to(classOf[AuthRecordDAO])

    bind(classOf[AuthRecordModule]).to(classOf[AuthRecords])
    bind(classOf[EnrolmentModule]).to(classOf[Enrolments])
    bind(classOf[LevyDeclarationModule]).to(classOf[LevyDeclarations])
  }

}
