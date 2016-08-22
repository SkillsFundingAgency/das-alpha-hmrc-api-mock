import com.google.inject.AbstractModule
import uk.gov.bis.levyApiMock.data.levy.{DummyFractions, EnrolmentOps, FractionsOps, LevyDeclarationOps}
import uk.gov.bis.levyApiMock.data.oauth2.AuthRecordOps
import uk.gov.bis.levyApiMock.db.levy.{EnrolmentDAO, EnrolmentModule}
import uk.gov.bis.levyApiMock.db.oauth2.{AuthRecordDAO, AuthRecordModule}
import uk.gov.bis.levyApiMock.mongo.LevyDeclarationMongo
import uk.gov.bis.levyApiMock.playslicks.{AuthRecords, Enrolments}

class Module extends AbstractModule {

  override def configure() = {
    bind(classOf[EnrolmentOps]).to(classOf[EnrolmentDAO])
    bind(classOf[LevyDeclarationOps]).to(classOf[LevyDeclarationMongo])
    bind(classOf[AuthRecordOps]).to(classOf[AuthRecordDAO])
    bind(classOf[FractionsOps]).to(classOf[DummyFractions])

    bind(classOf[AuthRecordModule]).to(classOf[AuthRecords])
    bind(classOf[EnrolmentModule]).to(classOf[Enrolments])
  }

}
