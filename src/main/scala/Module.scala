import com.google.inject.AbstractModule
import uk.gov.bis.data.levy.{EnrolmentOps, LevyDeclarationOps}
import uk.gov.bis.data.oauth2.AuthRecordOps
import uk.gov.bis.db.levy.{EnrolmentDAO, EnrolmentModule, LevyDeclarationDAO, LevyDeclarationModule}
import uk.gov.bis.db.oauth2.{AuthRecordDAO, AuthRecordModule}
import uk.gov.bis.playslicks.{AuthRecords, Enrolments, LevyDeclarations}

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
