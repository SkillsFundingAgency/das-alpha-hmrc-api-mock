package controllers.api

import javax.inject.Inject

import actions.api.OpenIDConnectAction
import cats.data.OptionT
import cats.std.future._
import data.levy.{EnrolmentOps, ServiceBinding}
import data.oauth2.AuthRecordOps
import play.api.libs.json._
import play.api.mvc.Controller

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
      case "openid:taxids" =>
        OptionT(authRecords.find(accessToken)).map { a =>
          enrolments.forGatewayId(a.gatewayId)
        }.value.flatMap {
          case Some(bf) => bf.map(bs => Seq("taxids" -> Json.toJson(bs.toList)))
          case None => Future.successful(Seq())
        }

      case _ => Future.successful(Seq())

    }
  }
}