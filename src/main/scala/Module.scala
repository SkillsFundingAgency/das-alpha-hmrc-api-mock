import com.google.inject.{AbstractModule, TypeLiteral}
import uk.gov.bis.levyApiMock.data._
import uk.gov.bis.levyApiMock.mongo._
import uk.gov.bis.levyApiMock.services.{EmploymentsImpl, EmploymentsRepo}
import uk.gov.bis.oauth.data._
import uk.gov.bis.oauth.mongo._

import scala.concurrent.Future

class Module extends AbstractModule {

  override def configure() = {
    bind(classOf[EmprefOps]).to(classOf[EmprefMongo])
    bind(classOf[LevyDeclarationOps]).to(classOf[LevyDeclarationMongo])
    bind(classOf[AuthRecordOps]).to(classOf[AuthRecordMongo])
    bind(classOf[FractionsOps]).to(classOf[FractionMongo])
    bind(classOf[FractionCalcOps]).to(classOf[FractionCalcMongo])
    bind(classOf[GatewayUserOps]).to(classOf[GatewayUserMongo])
    bind(classOf[AuthCodeOps]).to(classOf[AuthCodeMongo])
    bind(classOf[AuthRequestOps]).to(classOf[AuthRequestMongo])
    bind(classOf[ClientOps]).to(classOf[ClientMongo])
    bind(classOf[ScopeOps]).to(classOf[ScopeMongo])

    bind(new TypeLiteral[EmploymentStatusOps[Future]] {}).to(classOf[EmploymentsImpl])
    bind(new TypeLiteral[EmploymentsRepo[Future]] {}).to(classOf[EmploymentsMongo])
  }
}
