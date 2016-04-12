package controllers.api

import javax.inject._

import actions.api.AuthorizedAction
import data.levy.LevyDeclarationOps
import models.{EnglishFraction, LevyDeclaration, LevyDeclarations, PayrollMonth}
import org.joda.time.LocalDate
import play.api.libs.json.Json
import play.api.mvc._
import uk.gov.hmrc.domain.EmpRef

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class LevyDeclarationController @Inject()(declarations: LevyDeclarationOps, AuthenticatedAction: AuthorizedAction)(implicit exec: ExecutionContext) extends Controller {

  def levyDeclarations(empref: EmpRef, months: Option[Int]) = AuthenticatedAction(empref.value, "read:apprenticeship-levy").async { implicit request =>
    listDeclarations(empref, months.getOrElse(36).min(36)).map(decls => Ok(Json.toJson(decls)))
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
      val decls = rows.map { d => LevyDeclaration(PayrollMonth(d.year, d.month), d.amount) }

      val englishFraction = EnglishFraction(0.83, new LocalDate)

      LevyDeclarations(empref, englishFraction, 15000, decls)
    }
  }
}
