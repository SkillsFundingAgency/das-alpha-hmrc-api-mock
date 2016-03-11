package controllers

import javax.inject._

import db.{LevyDeclarationDAO, SchemeDAO}
import models.{EnglishFraction, LevyDeclaration, LevyDeclarations, PayrollMonth}
import org.joda.time.LocalDate
import play.api.libs.json.Json
import play.api.mvc._
import uk.gov.hmrc.domain.EmpRef

import scala.concurrent.ExecutionContext


@Singleton
class ApiController @Inject()(schemeDAO: SchemeDAO, levyDeclarationDAO: LevyDeclarationDAO)(implicit exec: ExecutionContext) extends Controller {
  def getLevyDeclarations(empref: EmpRef) = Action.async {

    levyDeclarationDAO.byEmpref(empref.value).map { ds =>
      val decls = ds.map { d =>
        LevyDeclaration(PayrollMonth(d.year, d.month), d.amount)
      }

      val englishFraction = EnglishFraction(0.83, new LocalDate)

      Ok(Json.toJson(LevyDeclarations(empref, englishFraction, decls)))
    }
  }

  def getSchemesForUtr(utr: String) = Action.async {
    schemeDAO.byUtr(utr).map { emprefs =>
      Ok(Json.toJson(emprefs.map(_.empref)))
    }
  }
}
