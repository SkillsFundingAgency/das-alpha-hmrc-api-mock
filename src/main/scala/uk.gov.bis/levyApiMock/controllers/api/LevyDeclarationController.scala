package uk.gov.bis.levyApiMock.controllers.api

import javax.inject._

import org.joda.time.LocalDate
import play.api.libs.json._
import play.api.mvc._
import uk.gov.bis.levyApiMock.actions.AuthorizedAction
import uk.gov.bis.levyApiMock.controllers.DateRange
import uk.gov.bis.levyApiMock.data.levy.{LevyDeclarationOps, LevyDeclarationResponse}
import uk.gov.hmrc.domain.EmpRef

import scala.concurrent.ExecutionContext

class LevyDeclarationController @Inject()(declarations: LevyDeclarationOps, AuthorizedAction: AuthorizedAction)(implicit exec: ExecutionContext)
  extends Controller {

  def levyDeclarations(empref: EmpRef, fromDate: Option[LocalDate], toDate: Option[LocalDate]) =
    AuthorizedAction(empref.value).async { implicit request =>
     // Action.async { implicit request =>

      val dateRange = DateRange(fromDate, toDate)
      declarations.byEmpref(empref.value).map {
        case Some(decls) => Ok(Json.toJson(filterByDate(decls, dateRange)))
        case None => NotFound
      }
    }

  def filterByDate(decls: LevyDeclarationResponse, dateRange: DateRange): LevyDeclarationResponse = {
    val filtered = decls.declarations.filter(d => dateRange.contains(d.submissionTime.toLocalDate))
    decls.copy(declarations = filtered)
  }

}
