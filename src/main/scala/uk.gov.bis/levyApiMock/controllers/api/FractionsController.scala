package uk.gov.bis.levyApiMock.controllers.api

import javax.inject._

import play.api.libs.json.Json
import play.api.mvc._
import uk.gov.bis.levyApiMock.api.AuthorizedAction
import uk.gov.bis.levyApiMock.data.levy.{Fraction, FractionCalculation, FractionsOps}
import uk.gov.hmrc.domain.EmpRef

import scala.concurrent.{ExecutionContext, Future}

class FractionsController @Inject()(fractions: FractionsOps, AuthorizedAction: AuthorizedAction)(implicit exec: ExecutionContext) extends Controller {

  implicit val fractionW = Json.writes[Fraction]
  implicit val fractionCalcW = Json.writes[FractionCalculation]

  def fractions(empref: EmpRef, months: Option[Int]) =
    AuthorizedAction("empref", empref.value, "read:apprenticeship-levy").async { implicit request =>
      listFractions(empref, months.getOrElse(48).min(48)).map(decls => Ok(Json.toJson(decls)))
    }

  def listFractions(empref: EmpRef, months: Int): Future[Seq[FractionCalculation]] = fractions.byEmpref(empref.value)
}
