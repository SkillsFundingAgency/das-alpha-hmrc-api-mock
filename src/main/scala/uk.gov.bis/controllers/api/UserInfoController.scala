package uk.gov.bis.controllers.api

import javax.inject.Inject

import cats.data.OptionT
import cats.std.future._
import play.api.libs.json._
import play.api.mvc.Controller
import uk.gov.bis.api.OpenIDConnectAction
import uk.gov.bis.data.levy.{EnrolmentOps, ServiceBinding}
import uk.gov.bis.data.oauth2.AuthRecordOps

import scala.concurrent.{ExecutionContext, Future}

class UserInfoController @Inject()(OpenIDConnectAction: OpenIDConnectAction, authRecords: AuthRecordOps, enrolments: EnrolmentOps)(implicit ec: ExecutionContext) extends Controller {

  def userInfo = OpenIDConnectAction().async { implicit request =>
    val info = request.claims.filter(_.startsWith("openid:")).map { claim =>
      processClaim(request.token, claim)
    }

    Future.sequence(info).map(cs => Ok(JsObject(cs.flatten)))
  }

  implicit val sbFormats = Json.format[ServiceBinding]

  def processClaim(accessToken: String, claim: String): Future[Seq[(String, JsValue)]] = {
    claim match {
      case "openid:enrolments" =>
        OptionT(authRecords.find(accessToken)).map { a =>
          enrolments.forGatewayId(a.gatewayId)
        }.value.flatMap {
          case Some(bf) => bf.map(bs => Seq("enrolments" -> Json.toJson(bs.toList)))
          case None => Future.successful(Seq())
        }

      case _ => Future.successful(Seq())

    }
  }
}