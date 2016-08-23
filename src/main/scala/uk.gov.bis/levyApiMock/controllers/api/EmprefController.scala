package uk.gov.bis.levyApiMock.controllers.api

import javax.inject.Inject

import play.api.libs.json.Json
import play.api.mvc.{Action, Controller}
import uk.gov.bis.levyApiMock.api.AuthorizedAction
import uk.gov.bis.levyApiMock.data.levy.EmprefOps
import uk.gov.bis.levyApiMock.mongo._
import uk.gov.hmrc.domain.EmpRef

import scala.concurrent.ExecutionContext

class EmprefController @Inject()(emprefs: EmprefOps, AuthorizedAction: AuthorizedAction)(implicit ec: ExecutionContext) extends Controller {

  implicit val empNameW = Json.writes[EmployerName]
  implicit val empW = Json.writes[Employer]
  implicit val hrefW = Json.writes[Href]
  implicit val respW = Json.writes[EmprefResponse]

  def empref(empref: EmpRef) =
    AuthorizedAction("empref", empref.value, "read:apprenticeship-levy").async { implicit request =>
      //  Action.async { implicit request =>
      emprefs.forEmpref(empref.value).map {
        case Some(resp) => Ok(Json.toJson(resp))
        case None => NotFound
      }
    }
}