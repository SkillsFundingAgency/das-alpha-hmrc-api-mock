package uk.gov.bis.levyApiMock.auth

import java.util.Date
import javax.inject.Inject

import cats.data.OptionT
import cats.instances.future._
import play.api.Logger
import play.api.libs.json.Json
import uk.gov.bis.levyApiMock.data._
import uk.gov.bis.levyApiMock.data.oauth2.{AuthRecord, AuthRecordOps}

import scala.concurrent.{ExecutionContext, Future}
import scalaoauth2.provider._

case class ServiceBinding(service: String, identifierType: String, identifier: String)

object ServiceBinding {
  implicit val formats = Json.format[ServiceBinding]
}

case class Token(value: String, scopes: List[String], gatewayId: String, enrolments: List[ServiceBinding], clientId: String, expiresAt: Long)

object Token {
  implicit val formats = Json.format[Token]
}

/**
  * Provides the behaviours needed by the OAuth2Provider to create and retrieve access tokens
  */
class APIDataHandler @Inject()(
                                applications: ClientOps,
                                authRecords: AuthRecordOps,
                                authCodes: AuthCodeOps,
                                gatewayUsers: GatewayUserOps)
                              (implicit ec: ExecutionContext)
  extends DataHandler[GatewayUser] {

  override def validateClient(request: AuthorizationRequest): Future[Boolean] = {
    Logger.debug("validate client")
    request.clientCredential match {
      case Some(cred) =>
        Logger.debug(cred.toString)
        applications.validate(cred.clientId, cred.clientSecret, request.grantType)
      case None => Future.successful(false)
    }
  }

  override def createAccessToken(authInfo: AuthInfo[GatewayUser]): Future[AccessToken] = {
    Logger.debug("create access token")
    val accessTokenExpiresIn = 60L * 60L
    // 1 hour
    val refreshToken = Some(generateToken)
    val accessToken = generateToken
    val createdAt = System.currentTimeMillis()
    val privileged = authInfo.user == privilegedActionUser
    val auth = AuthRecord(accessToken, refreshToken, authInfo.user.gatewayID, authInfo.scope, accessTokenExpiresIn, createdAt, authInfo.clientId.get, Some(privileged))

    for {
      _ <- authRecords.create(auth)
    } yield AccessToken(auth.accessToken, auth.refreshToken, auth.scope, Some(auth.expiresIn), new Date(auth.createdAt))
  }

  override def refreshAccessToken(authInfo: AuthInfo[GatewayUser], refreshToken: String): Future[AccessToken] = {
    Logger.debug("refresh access token")
    val accessTokenExpiresIn = Some(60L * 60L)
    // 1 hour
    val accessToken = generateToken
    val createdAt = System.currentTimeMillis()

    authRecords.forRefreshToken(refreshToken).flatMap {
      case Some(authRecord) =>
        val updatedRow = authRecord.copy(accessToken = accessToken, createdAt = createdAt)
        for {
          _ <- authRecords.deleteExistingAndCreate(updatedRow)
        } yield AccessToken(updatedRow.accessToken, Some(refreshToken), authInfo.scope, accessTokenExpiresIn, new Date(createdAt))
      case None =>
        val s = s"Cannot find an access token entry with refresh token $refreshToken"
        Logger.warn(s)
        throw new IllegalArgumentException(s)
    }
  }

  override def findAuthInfoByRefreshToken(refreshToken: String): Future[Option[AuthInfo[GatewayUser]]] = {
    Logger.debug("find auth info by refresh token")
    for {
      at <- OptionT(authRecords.forRefreshToken(refreshToken))
      u <- OptionT(gatewayUsers.forGatewayID(at.gatewayID))
    } yield AuthInfo(u, Some(at.clientID), at.scope, None)
  }.value


  override def getStoredAccessToken(authInfo: AuthInfo[GatewayUser]): Future[Option[AccessToken]] = {
    Logger.debug("get stored access token using AuthInfo")
    OptionT(authRecords.find(authInfo.user.gatewayID, authInfo.clientId)).map { token =>
      AccessToken(token.accessToken, token.refreshToken, token.scope, Some(token.expiresIn), new Date(token.createdAt))
    }
  }.value

  override def findAccessToken(token: String): Future[Option[AccessToken]] = {
    Logger.debug("find access token by String")
    OptionT(authRecords.forAccessToken(token)).map { auth =>
      AccessToken(auth.accessToken, auth.refreshToken, auth.scope, Some(auth.expiresIn), new Date(auth.createdAt))
    }
  }.value

  override def findAuthInfoByCode(code: String): Future[Option[AuthInfo[GatewayUser]]] = {
    Logger.debug("find auth info by code")
    for {
      token <- OptionT(authCodes.find(code))
      _ = Logger.debug(token.toString)
      user <- OptionT(gatewayUsers.forGatewayID(token.gatewayId))
      _ = Logger.debug(user.toString)
    } yield AuthInfo(user, token.clientId, token.scope, None)
  }.value

  override def deleteAuthCode(code: String): Future[Unit] = {
    Logger.debug("delete auth code")
    authCodes.delete(code).map(_ => ())
  }

  override def findAuthInfoByAccessToken(accessToken: AccessToken): Future[Option[AuthInfo[GatewayUser]]] = {
    Logger.debug("find auth info by access token")
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
    Logger.debug("find user by AuthorizationRequest")

    for {
      cred <- OptionT.fromOption(request.clientCredential)
      app <- OptionT(applications.forId(cred.clientId)) if checkPrivilegedAccess(cred, app)
    } yield privilegedActionUser

  }.value

  private def checkPrivilegedAccess(cred: ClientCredential, app: Application): Boolean = {
    cred.clientSecret.exists { cs =>
      TOTP.generateCodesAround(app.clientSecret, System.currentTimeMillis()).contains(TOTPCode(cs))
    }
  }
}
