import com.google.inject.AbstractModule
import uk.gov.bis.levyApiMock.data._
import uk.gov.bis.levyApiMock.data.levy._
import uk.gov.bis.levyApiMock.data.oauth2.AuthRecordOps
import uk.gov.bis.levyApiMock.mongo._

class Module extends AbstractModule {

  override def configure() = {
    bind(classOf[EmprefOps]).to(classOf[EmprefMongo])
    bind(classOf[LevyDeclarationOps]).to(classOf[LevyDeclarationMongo])
    bind(classOf[AuthRecordOps]).to(classOf[AuthRecordMongo])
    bind(classOf[FractionsOps]).to(classOf[FractionMongo])
    bind(classOf[GatewayUserOps]).to(classOf[GatewayUserMongo])
    bind(classOf[AccessTokenOps]).to(classOf[AccessTokenMongo])
    bind(classOf[AuthCodeOps]).to(classOf[AuthCodeMongo])
    bind(classOf[AuthRequestOps]).to(classOf[AuthRequestMongo])
    bind(classOf[ClientOps]).to(classOf[ClientMongo])
    bind(classOf[ScopeOps]).to(classOf[ScopeMongo])

  }

}
