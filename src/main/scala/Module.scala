import com.google.inject.AbstractModule
import uk.gov.bis.levyApiMock.data.levy.{EnrolmentOps, LevyDeclarationOps}
import uk.gov.bis.levyApiMock.data.oauth2.AuthRecordOps
import uk.gov.bis.levyApiMock.db.levy.{EnrolmentDAO, EnrolmentModule, LevyDeclarationDAO, LevyDeclarationModule}
import uk.gov.bis.levyApiMock.db.oauth2.{AuthRecordDAO, AuthRecordModule}
import uk.gov.bis.levyApiMock.playslicks.{AuthRecords, Enrolments, LevyDeclarations}

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
