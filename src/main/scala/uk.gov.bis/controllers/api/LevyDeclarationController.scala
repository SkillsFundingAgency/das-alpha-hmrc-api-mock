package uk.gov.bis.controllers.api

import javax.inject._

import org.joda.time.LocalDate
import play.api.libs.json.Json
import play.api.mvc._
import uk.gov.bis.api.AuthorizedAction
import uk.gov.bis.data.levy.LevyDeclarationOps
import uk.gov.bis.models.{EnglishFraction, LevyDeclaration, LevyDeclarations, PayrollMonth}
import uk.gov.hmrc.domain.EmpRef

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class LevyDeclarationController @Inject()(declarations: LevyDeclarationOps, AuthorizedAction: AuthorizedAction)(implicit exec: ExecutionContext) extends Controller {

  def levyDeclarations(empref: EmpRef, months: Option[Int]) =
    AuthorizedAction("empref", empref.value, "read:apprenticeship-levy").async { implicit request =>
      listDeclarations(empref, months.getOrElse(48).min(48)).map(decls => Ok(Json.toJson(decls)))
    }

  /**
    * Build a LevyDeclarations structure for the empref for up to the given number of months
    *
    * @param empref identifies the payroll scheme
    * @param months maximum number of months of data to return (will be limited to 36)
    * @return
    */
  def listDeclarations(empref: EmpRef, months: Int): Future[LevyDeclarations] = {
    declarations.byEmpref(empref.value).map { rows =>


      val decls = rows.zipWithIndex.map { case (d, i) =>
        val englishFraction = EnglishFraction((0.75 + 0.02 * i).min(0.80), new LocalDate)
        LevyDeclaration(PayrollMonth(d.year, d.month), d.amount, d.submissionType, d.submissionDate, englishFraction)
      }

      LevyDeclarations(empref, levyAllowanceApplied = true, decls)
    }
  }
}
