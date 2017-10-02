package uk.gov.bis.levyApiMock.controllers

import javax.inject.Inject

import play.api.libs.json.Json
import play.api.mvc.Controller
import uk.gov.bis.levyApiMock.data._
import uk.gov.bis.oauth.actions.AuthorizedAction
import uk.gov.hmrc.domain.EmpRef

import scala.concurrent.ExecutionContext

class EmprefController @Inject()(emprefs: EmprefOps, AuthorizedAction: AuthorizedAction)(implicit ec: ExecutionContext) extends Controller {

  implicit val empNameW = Json.writes[EmployerName]
  implicit val empW = Json.writes[Employer]
  implicit val hrefW = Json.writes[Href]
  implicit val respW = Json.writes[EmprefResponse]

  def empref(empref: EmpRef) =
    AuthorizedAction(empref.value).async { implicit request =>
      emprefs.forEmpref(empref.value).map {
        case Some(resp) => Ok(Json.toJson(resp))
        case None => NotFound
      }
    }
}