package auth

import java.security.SecureRandom
import java.sql.Date
import javax.inject.Inject

import db._
import db.outh2._
import org.apache.commons.codec.binary.Hex
import org.mindrot.jbcrypt.BCrypt

import scala.concurrent.{ExecutionContext, Future}
import scalaoauth2.provider._


class DASDataHandler @Inject()(implicit ec: ExecutionContext, users: UserDAO, clients: ClientDAO, accessTokens: AccessTokenDAO) extends DataHandler[UserRow] {
  override def validateClient(request: AuthorizationRequest): Future[Boolean] = {
    request.clientCredential match {
      case Some(cred) => clients.validate(cred.clientId, cred.clientSecret, request.grantType)
      case None => Future.successful(false)
    }
  }

  private val random = new SecureRandom()
  random.nextBytes(new Array[Byte](55))

  def generateToken: String = {
    val bytes = new Array[Byte](12)
    random.nextBytes(bytes)
    new String(Hex.encodeHex(bytes))
  }

  override def createAccessToken(authInfo: AuthInfo[UserRow]): Future[AccessToken] = {
    val accessTokenExpiresIn = Some(60L * 60L) // 1 hour
    val refreshToken = Some(generateToken)
    val accessToken = generateToken
    val createdAt = new Date(System.currentTimeMillis())
    val tokenObject = AccessTokenRow(accessToken, refreshToken, authInfo.user.id, authInfo.scope, accessTokenExpiresIn, createdAt, authInfo.clientId)
    accessTokens.deleteExistingAndCreate(tokenObject).map { _ =>
      AccessToken(accessToken, refreshToken, authInfo.scope, accessTokenExpiresIn, createdAt)
    }
  }

  override def refreshAccessToken(authInfo: AuthInfo[UserRow], refreshToken: String): Future[AccessToken] = ???

  override def findAuthInfoByRefreshToken(refreshToken: String): Future[Option[AuthInfo[UserRow]]] = ???

  override def getStoredAccessToken(authInfo: AuthInfo[UserRow]): Future[Option[AccessToken]] = ???

  override def findAuthInfoByCode(code: String): Future[Option[AuthInfo[UserRow]]] = ???

  override def deleteAuthCode(code: String): Future[Unit] = ???

  override def findAuthInfoByAccessToken(accessToken: AccessToken): Future[Option[AuthInfo[UserRow]]] = ???

  override def findAccessToken(token: String): Future[Option[AccessToken]] = ???

  override def findUser(request: AuthorizationRequest): Future[Option[UserRow]] = {
    request.clientCredential.map { cred =>
      users.byName(cred.clientId).map(_.filter(u => BCrypt.checkpw(cred.clientSecret.get, u.hashedPassword)))
    } match {
      case None => Future.successful(None)
      case Some(f) => f
    }
  }
}
