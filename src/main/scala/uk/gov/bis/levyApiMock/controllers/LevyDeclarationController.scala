package uk.gov.bis.levyApiMock.controllers

import javax.inject._

import org.joda.time.LocalDate
import play.api.libs.json._
import play.api.mvc._
import uk.gov.bis.levyApiMock.data.{LevyDeclarationOps, LevyDeclarationResponse}
import uk.gov.bis.oauth.actions.AuthorizedAction
import uk.gov.hmrc.domain.EmpRef

import scala.concurrent.ExecutionContext

class LevyDeclarationController @Inject()(declarations: LevyDeclarationOps, AuthorizedAction: AuthorizedAction)(implicit exec: ExecutionContext)
  extends Controller {

  implicit class LevyDeclarationsSyntax(resp: LevyDeclarationResponse) {
    def filter(dateRange: DateRange): LevyDeclarationResponse = {
      val filtered = resp.declarations.filter(d => dateRange.contains(d.submissionTime.toLocalDate))
      resp.copy(declarations = filtered)
    }

    def sort: LevyDeclarationResponse = {
      val sorted = resp.declarations.sortBy(_.id).reverse
      resp.copy(declarations = sorted)
    }
  }

  def levyDeclarations(empref: EmpRef, fromDate: Option[LocalDate], toDate: Option[LocalDate]) =
    AuthorizedAction(empref.value).async { implicit request =>
      val dateRange = DateRange(fromDate, toDate)
      declarations.byEmpref(empref.value).map {
        case Some(decls) => Ok(Json.toJson(decls.filter(dateRange).sort))
        case None => NotFound
      }
    }

}
