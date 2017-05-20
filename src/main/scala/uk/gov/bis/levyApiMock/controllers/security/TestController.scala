package uk.gov.bis.levyApiMock.controllers.security

import play.api.libs.json.{JsBoolean, JsObject, JsString}
import play.api.mvc.{Action, Controller}
import uk.gov.bis.levyApiMock.auth.TOTP

class TestController extends Controller {

  def generateToken(secret: String, ts: Option[Long]) = Action {
    val token = TOTP.generateCodeAtTime(secret, ts.getOrElse(System.currentTimeMillis())).value
    Ok(JsObject(Seq("token" -> JsString(token))))
  }

  def checkToken(secret: String, token: String, ts: Option[Long]) = Action {
    val generatedToken = TOTP.generateCodeAtTime(secret, ts.getOrElse(System.currentTimeMillis())).value

    val valid = generatedToken == token
    Ok(JsObject(Seq("valid" -> JsBoolean(valid))))
  }

}
