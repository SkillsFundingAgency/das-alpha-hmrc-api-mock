package uk.gov.bis.levyApiMock.controllers.api

import javax.inject._

import org.joda.time.LocalDate
import play.api.libs.json._
import play.api.mvc._
import uk.gov.bis.levyApiMock.api.AuthorizedAction
import uk.gov.bis.levyApiMock.data.levy.LevyDeclarationOps
import uk.gov.bis.levyApiMock.models.LevyDeclarations
import uk.gov.hmrc.domain.EmpRef

import scala.concurrent.ExecutionContext

class LevyDeclarationController @Inject()(declarations: LevyDeclarationOps, AuthorizedAction: AuthorizedAction)(implicit exec: ExecutionContext)
  extends Controller {

  def levyDeclarations(empref: EmpRef, fromDate: Option[LocalDate], toDate: Option[LocalDate]) =
    AuthorizedAction("empref", empref.value, "read:apprenticeship-levy").async { implicit request =>
      //Action.async { implicit request =>
      declarations.byEmpref(empref.value).map {
        case Some(decls) => Ok(Json.toJson(filterByDate(decls, fromDate, toDate)))
        case None => NotFound
      }
    }

  def filterByDate(decls: LevyDeclarations, fromDate: Option[LocalDate], toDate: Option[LocalDate]): LevyDeclarations = {
    val filtered = decls.declarations.flatMap { decl =>
      (decl, fromDate, toDate) match {
        case (d, None, None) => Some(d)
        case (d, Some(from), None) if !d.submissionTime.toLocalDate.isBefore(from) => Some(d)
        case (d, None, Some(to)) if !d.submissionTime.toLocalDate.isAfter(to) => Some(d)
        case (d, Some(from), Some(to)) if !d.submissionTime.toLocalDate.isBefore(from) && !d.submissionTime.toLocalDate.isAfter(to) => Some(d)
        case _ => None
      }
    }

    decls.copy(declarations = filtered)
  }

}
