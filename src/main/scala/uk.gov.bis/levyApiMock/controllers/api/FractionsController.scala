package uk.gov.bis.levyApiMock.controllers.api

import javax.inject._

import org.joda.time.LocalDate
import play.api.libs.json.{Json, Reads, Writes}
import play.api.mvc._
import uk.gov.bis.levyApiMock.actions.AuthorizedAction
import uk.gov.bis.levyApiMock.controllers.DateRange
import uk.gov.bis.levyApiMock.data.levy._
import uk.gov.hmrc.domain.EmpRef

import scala.concurrent.ExecutionContext

class FractionsController @Inject()(
                                     fractionOps: FractionsOps,
                                     fractionCalcOps: FractionCalcOps,
                                     AuthorizedAction: AuthorizedAction
                                   )(implicit exec: ExecutionContext) extends Controller {

  implicit class FractionResponseSyntax(resp: FractionResponse) {
    def filter(dateRange: DateRange): FractionResponse = {
      val filtered = resp.fractionCalculations.filter(f => dateRange.contains(f.calculatedAt))
      resp.copy(fractionCalculations = filtered)
    }

    def sort: FractionResponse = {
      val sorted = resp.fractionCalculations.sortBy(_.calculatedAt.toDate.getTime).reverse
      resp.copy(fractionCalculations = sorted)
    }
  }

  implicit val fractionW = Json.writes[Fraction]
  implicit val fractionCalcW = Json.writes[FractionCalculation]
  implicit val fractionRepsonseW = Json.writes[FractionResponse]

  def fractions(empref: EmpRef, fromDate: Option[LocalDate], toDate: Option[LocalDate]) =
    AuthorizedAction(empref.value).async { implicit request =>
      val dateRange = DateRange(fromDate, toDate)
      fractionOps.byEmpref(empref.value).map {
        case Some(fs) => Ok(Json.toJson(fs.filter(dateRange).sort))
        case None => NotFound
      }
    }

  def calculationDate = Action.async { implicit request =>
    implicit val jldWrites = Writes.jodaLocalDateWrites("yyyy-MM-dd")
    fractionCalcOps.lastCalculationDate.map {
      case Some(d) => Ok(Json.toJson(d))
      case None => NotFound
    }
  }


}
