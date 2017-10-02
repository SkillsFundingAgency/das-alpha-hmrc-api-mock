package uk.gov.bis.controllers

import play.api.libs.json.Json
import play.api.mvc.{Action, Controller}
import uk.gov.bis.levyApiMock.buildinfo.BuildInfo

class HealthCheckController extends Controller {
  def ping = Action {
    Ok("alive")
  }

  def version = Action {
    // need to convert the Anys to Strings so play json knows how to
    // convert it
    Ok(Json.toJson(BuildInfo.toMap.mapValues(_.toString)))
  }
}
