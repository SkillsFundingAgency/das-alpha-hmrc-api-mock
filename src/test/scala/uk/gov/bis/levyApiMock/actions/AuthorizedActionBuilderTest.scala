package uk.gov.bis.levyApiMock.actions

import org.scalatest.concurrent.ScalaFutures
import org.scalatest.{Matchers, OptionValues, WordSpecLike}
import uk.gov.bis.levyApiMock.data.{GatewayUser, MongoDate}
import uk.gov.bis.levyApiMock.data.oauth2.AuthRecord

import scala.concurrent.{ExecutionContext, Future}

class AuthorizedActionBuilderTest extends WordSpecLike with Matchers with OptionValues with ScalaFutures {

  private val testEmpref = "123/AB12345"
  private val testToken = "12334567890"
  private val testUsername = "user"
  val testAuthRecord = AuthRecord(testToken, None, testUsername, None, 0, MongoDate("2016-09-07T16:11:14Z"), "", Some(false))
  val testUser = GatewayUser(testUsername, "", testEmpref, None, None)

  val records = Map(testToken->testAuthRecord)
  val users = Map(testUsername->testUser)

  val sut = new AuthorizedActionBuilder(testEmpref, new DummyAuthRecords(records), new DummyGatewayUsers(users))(ExecutionContext.global)

  "non-privileged user with right empref" should {
    "have access" in {
      sut.validateToken(testToken).futureValue.value shouldBe testAuthRecord
    }
  }
}

class DummyGatewayUsers(users:Map[String, GatewayUser]) extends StubGatewayUsers {
  override def forGatewayID(gatewayID: String)(implicit ec: ExecutionContext): Future[Option[GatewayUser]] =
    Future.successful(users.get(gatewayID))
}

class DummyAuthRecords(records: Map[String, AuthRecord]) extends StubAuthRecords {
  override def find(accessToken: String)(implicit ec: ExecutionContext): Future[Option[AuthRecord]] =
    Future.successful(records.get(accessToken))
}