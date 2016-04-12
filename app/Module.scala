import com.google.inject.AbstractModule
import data.levy.{GatewayIdSchemeOps, LevyDeclarationOps}
import data.oauth2.AuthRecordOps
import db.levy.{GatewayIdSchemeDAO, LevyDeclarationDAO}
import db.oauth2.AuthRecordDAO

class Module extends AbstractModule {

  override def configure() = {
    bind(classOf[GatewayIdSchemeOps]).to(classOf[GatewayIdSchemeDAO])
    bind(classOf[LevyDeclarationOps]).to(classOf[LevyDeclarationDAO])
    bind(classOf[AuthRecordOps]).to(classOf[AuthRecordDAO])
  }

}
