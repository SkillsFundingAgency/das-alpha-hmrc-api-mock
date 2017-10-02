package uk.gov.bis.levyApiMock.controllers

import javax.inject.Inject

import play.api.libs.json.Json
import play.api.mvc.Controller
import uk.gov.bis.levyApiMock.data.Href
import uk.gov.bis.oauth.actions.AuthenticatedAction
import uk.gov.bis.oauth.data.GatewayUserOps

import scala.concurrent.ExecutionContext

case class RootResponse(_links: Map[String, Href], emprefs: Seq[String])

class RootController @Inject()(users: GatewayUserOps, authenticatedAction: AuthenticatedAction)(implicit ec: ExecutionContext) extends Controller {
  implicit def hrefW = Json.writes[Href]

  implicit def rootW = Json.writes[RootResponse]

  def root = authenticatedAction.async { request =>
    users.forGatewayID(request.authRecord.gatewayID).map {
      case Some(user) => Ok(Json.toJson(RootResponse(buildLinks(user.empref), user.empref.toSeq)))
      case _ => NotFound
    }
  }

  def buildLinks(empref: Option[String]): Map[String, Href] = {
    import views.html.helper.urlEncode
    Seq(
      Some("self" -> Href("/")),
      empref.map(e => e -> Href(s"/epaye/${urlEncode(e)}"))
    ).flatten.toMap
  }
}
