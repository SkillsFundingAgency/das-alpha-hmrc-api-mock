package controllers

import javax.inject._

import models.{LevyDeclaration, LevyDeclarations, PayrollMonth}
import play.api.libs.json.Json
import play.api.mvc._
import uk.gov.hmrc.domain.EmpRef

import scala.concurrent.{ExecutionContext, Future}


@Singleton
class ApiController @Inject()(implicit exec: ExecutionContext) extends Controller {


  def getLevyDeclarations(empref: EmpRef) = Action.async {
    val declarations = Seq(LevyDeclaration(PayrollMonth(2016, 1), BigDecimal(3200)))
    val levyData = new LevyDeclarations(empref, declarations)

    Future.successful(Ok(Json.toJson(levyData)))
  }

  def getSchemesForUtr(utr: String) = Action.async {
    val emprefs = Seq("123/AB12345", "321/ZX54321")

    Future.successful(Ok(Json.toJson(emprefs)))
  }


}
