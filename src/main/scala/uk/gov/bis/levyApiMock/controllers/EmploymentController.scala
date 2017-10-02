package uk.gov.bis.levyApiMock.controllers

import javax.inject.Inject

import org.joda.time.LocalDate
import play.api.libs.json.Json
import play.api.mvc.Controller
import uk.gov.bis.levyApiMock.data.EmploymentStatusOps
import uk.gov.bis.oauth.actions.EmprefAuthorizedAction
import uk.gov.hmrc.domain.EmpRef

import scala.concurrent.{ExecutionContext, Future}

case class EmploymentCheckResult(empref: String, nino: String, fromDate: LocalDate, toDate: LocalDate, employed: Boolean)

object EmploymentCheckResult {
  implicit val formats = Json.format[EmploymentCheckResult]
}


class EmploymentController @Inject()(employment: EmploymentStatusOps[Future], AuthorizedAction: EmprefAuthorizedAction)(implicit ec: ExecutionContext) extends Controller {
  def employmentCheck(empref: EmpRef, nino: String, fromDate: LocalDate, toDate: LocalDate) = AuthorizedAction(empref.value).async { implicit request =>
    ClosedDateRange.fromDates(fromDate, toDate) match {
      case Some(dateRange) => employment.employed(empref.value, nino, dateRange).map {
        case Some(result) => Ok(Json.toJson(result))
        case None => NotFound(Json.obj("code" -> "EPAYE_UNKNOWN", "message" -> "The provided NINO or EMPREF was not recognised"))
      }
      case None => Future.successful(BadRequest(Json.obj("code" -> "BAD_DATE_RANGE", "message" -> "The fromDate cannot be before the toDate")))
    }
  }
}

