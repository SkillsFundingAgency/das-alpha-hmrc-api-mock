package uk.gov.bis.levyApiMock.controllers.api

import javax.inject.Inject

import play.api.libs.json.Json
import play.api.mvc.Controller
import uk.gov.bis.levyApiMock.actions.AuthenticatedAction
import uk.gov.bis.levyApiMock.data.GatewayUserOps
import uk.gov.bis.levyApiMock.data.levy.Href

import scala.concurrent.ExecutionContext

case class RootResponse(_links: Map[String, Href], emprefs: Seq[String])

class RootController @Inject()(users: GatewayUserOps, authenticatedAction: AuthenticatedAction)(implicit ec: ExecutionContext) extends Controller {
  implicit def hrefW = Json.writes[Href]

  implicit def rootW = Json.writes[RootResponse]

  def root = authenticatedAction.async { request =>
    users.forGatewayID(request.authRecord.gatewayID).map {
      case Some(user) => Ok(Json.toJson(RootResponse(buildLinks(user.empref), Seq(user.empref))))
      case _ => NotFound
    }
  }

  def buildLinks(empref: String): Map[String, Href] = {
    import views.html.helper.urlEncode
    Map(
      "self" -> Href("/"),
      empref -> Href(s"/epaye/${urlEncode(empref)}")
    )
  }
}
