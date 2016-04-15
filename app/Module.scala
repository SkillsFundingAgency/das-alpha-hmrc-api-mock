import com.google.inject.AbstractModule
import data.levy.{EnrolmentOps, LevyDeclarationOps}
import data.oauth2.AuthRecordOps
import db.levy.{EnrolmentDAO, LevyDeclarationDAO}
import db.oauth2.AuthRecordDAO

class Module extends AbstractModule {

  override def configure() = {
    bind(classOf[EnrolmentOps]).to(classOf[EnrolmentDAO])
    bind(classOf[LevyDeclarationOps]).to(classOf[LevyDeclarationDAO])
    bind(classOf[AuthRecordOps]).to(classOf[AuthRecordDAO])
  }

}
