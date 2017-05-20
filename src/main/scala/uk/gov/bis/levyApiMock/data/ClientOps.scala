package uk.gov.bis.levyApiMock.data

import uk.gov.bis.levyApiMock.auth.{TOTP, TOTPCode}

import scala.concurrent.{ExecutionContext, Future}

case class Application(name: String, applicationID: String, clientID: String, clientSecret: String, serverToken: String, privilegedAccess: Boolean)

trait ClientOps {
  def timeSource : TimeSource

  def forId(clientID: String)(implicit ec: ExecutionContext): Future[Option[Application]]

  def validate(id: String, secret: Option[String], grantType: String)(implicit ec: ExecutionContext): Future[Boolean] =
    forId(id).map {
      _.exists { app =>
        if (app.privilegedAccess) secret.exists(checkPrivilegedAccess(_, app.clientSecret))
        else secret.contains(app.clientSecret)
      }
    }

  private def checkPrivilegedAccess(suppliedSecret: String, appSecret: String): Boolean = {
    val generateCodesAround = TOTP.generateCodesAround(appSecret, timeSource.currentTimeMillis())
    generateCodesAround.contains(TOTPCode(suppliedSecret))
  }
}
