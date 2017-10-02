package uk.gov.bis.oauth.auth

import java.util.Date
import javax.inject.Inject

import cats.data.OptionT
import cats.instances.future._
import uk.gov.bis.oauth.data._
import uk.gov.bis.utils.TimeSource

import scala.concurrent.{ExecutionContext, Future}
import scalaoauth2.provider._

/**
  * Provides the behaviours needed by the OAuth2Provider to create and retrieve access tokens
  */
class APIDataHandler @Inject()(
                                val applications: ClientOps,
                                val authRecords: AuthRecordOps,
                                authCodes: AuthCodeOps,
                                gatewayUsers: GatewayUserOps,
                                val timeSource: TimeSource
                              )
                              (implicit val ec: ExecutionContext)
  extends DataHandler[GatewayUser]
    with ValidateClientHandler
    with CreateAccessTokenHandler
    with RefreshAccessTokenHandler {


  override def findAuthInfoByRefreshToken(refreshToken: String): Future[Option[AuthInfo[GatewayUser]]] = {
    OAuthTrace("find auth info by refresh token")
    for {
      at <- OptionT(authRecords.forRefreshToken(refreshToken))
      u <- OptionT(gatewayUsers.forGatewayID(at.gatewayID))
    } yield AuthInfo(u, Some(at.clientID), at.scope, None)
  }.value


  override def getStoredAccessToken(authInfo: AuthInfo[GatewayUser]): Future[Option[AccessToken]] = {
    OAuthTrace("get stored access token using AuthInfo")
    OptionT(authRecords.find(authInfo.user.gatewayID, authInfo.clientId)).map { token =>
      AccessToken(token.accessToken, token.refreshToken, token.scope, Some(token.expiresIn), new Date(token.createdAt))
    }
  }.value

  override def findAccessToken(token: String): Future[Option[AccessToken]] = {
    OAuthTrace("find access token by String")
    OptionT(authRecords.forAccessToken(token)).map { auth =>
      AccessToken(auth.accessToken, auth.refreshToken, auth.scope, Some(auth.expiresIn), new Date(auth.createdAt))
    }
  }.value

  override def findAuthInfoByCode(code: String): Future[Option[AuthInfo[GatewayUser]]] = {
    OAuthTrace("find auth info by code")
    for {
      token <- OptionT(authCodes.find(code))
      _ = OAuthTrace(token.toString)
      user <- OptionT(gatewayUsers.forGatewayID(token.gatewayId))
      _ = OAuthTrace(user.toString)
    } yield AuthInfo(user, token.clientId, token.scope, None)
  }.value

  override def deleteAuthCode(code: String): Future[Unit] = {
    OAuthTrace("delete auth code")
    authCodes.delete(code).map(_ => ())
  }

  override def findAuthInfoByAccessToken(accessToken: AccessToken): Future[Option[AuthInfo[GatewayUser]]] = {
    OAuthTrace("find auth info by access token")
    for {
      token <- OptionT(authRecords.forAccessToken(accessToken.token))
      user <- OptionT(gatewayUsers.forGatewayID(token.gatewayID))
    } yield AuthInfo(user, Some(token.clientID), token.scope, None)
  }.value


  /**
    * findUser is used by the ClientCredentials handler. We only use this in the
    * case of Privileged Access
    */
  override def findUser(request: AuthorizationRequest): Future[Option[GatewayUser]] = {
    OAuthTrace("find user by AuthorizationRequest")

    for {
      cred <- OptionT.fromOption(request.clientCredential)
      app <- OptionT(applications.forId(cred.clientId)) if checkPrivilegedAccess(cred, app)
    } yield privilegedActionUser
  }.value

  private def checkPrivilegedAccess(cred: ClientCredential, app: Application): Boolean = {
    cred.clientSecret.exists { cs =>
      TOTP.generateCodesAround(app.clientSecret, timeSource.currentTimeMillis()).contains(TOTPCode(cs))
    }
  }
}
