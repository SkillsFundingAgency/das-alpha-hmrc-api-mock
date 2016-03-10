package controllers

import javax.inject._

import db.SchemeDAO
import models.{LevyDeclaration, LevyDeclarations, PayrollMonth}
import play.api.libs.json.Json
import play.api.mvc._
import uk.gov.hmrc.domain.EmpRef

import scala.concurrent.{ExecutionContext, Future}


@Singleton
class ApiController @Inject()(schemeDAO: SchemeDAO)(implicit exec: ExecutionContext) extends Controller {
  def getLevyDeclarations(empref: EmpRef) = Action.async {
    val declarations = Seq(LevyDeclaration(PayrollMonth(2016, 1), BigDecimal(3200)))
    val levyData = new LevyDeclarations(empref, declarations)

    Future.successful(Ok(Json.toJson(levyData)))
  }

  def getSchemesForUtr(utr: String) = Action.async {
    schemeDAO.byUtr(utr).map { emprefs =>
      Ok(Json.toJson(emprefs.map(_.empref)))
    }
  }
}
