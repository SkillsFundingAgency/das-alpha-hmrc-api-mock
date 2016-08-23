package uk.gov.bis.levyApiMock.controllers.api

import javax.inject._

import org.joda.time.LocalDate
import play.api.libs.json.Json
import play.api.mvc._
import uk.gov.bis.levyApiMock.actions.AuthorizedAction
import uk.gov.bis.levyApiMock.controllers.DateRange
import uk.gov.bis.levyApiMock.data.levy.{Fraction, FractionCalculation, FractionResponse, FractionsOps}
import uk.gov.hmrc.domain.EmpRef

import scala.concurrent.ExecutionContext

class FractionsController @Inject()(fractionOps: FractionsOps, AuthorizedAction: AuthorizedAction)(implicit exec: ExecutionContext) extends Controller {

  implicit val fractionW = Json.writes[Fraction]
  implicit val fractionCalcW = Json.writes[FractionCalculation]
  implicit val fractionRepsonseW = Json.writes[FractionResponse]

  def fractions(empref: EmpRef, fromDate: Option[LocalDate], toDate: Option[LocalDate]) =
    AuthorizedAction(empref.value).async { implicit request =>
      val dateRange = DateRange(fromDate, toDate)
      fractionOps.byEmpref(empref.value).map {
        case Some(fs) => Ok(Json.toJson(filterByDate(fs, dateRange)))
        case None => NotFound
      }
    }

  def filterByDate(resp: FractionResponse, dateRange: DateRange): FractionResponse = {
    val filtered = resp.fractionCalculations.filter(f => dateRange.contains(f.calculatedAt))
    resp.copy(fractionCalculations = filtered)
  }
}
