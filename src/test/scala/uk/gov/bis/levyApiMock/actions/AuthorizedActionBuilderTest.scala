package uk.gov.bis.levyApiMock.actions

import org.scalatest.concurrent.ScalaFutures
import org.scalatest.{Matchers, OptionValues, WordSpecLike}
import uk.gov.bis.levyApiMock.data.{GatewayUser, SystemTimeSource}
import uk.gov.bis.levyApiMock.data.oauth2.AuthRecord

import scala.concurrent.{ExecutionContext, Future}

class AuthorizedActionBuilderTest extends WordSpecLike with Matchers with OptionValues with ScalaFutures {

  private val empref1 = "123/AB12345"
  private val empref2 = "321/XY12345"

  private val testUsername = "user"
  private val testUser = GatewayUser(testUsername, "", empref1, None, None)

  private val nonPrivilegedToken = "12334567890"
  private val nonPrivilegedAuthRecord = AuthRecord(nonPrivilegedToken, None, testUsername, None, 3600, System.currentTimeMillis(), "", Some(false))
  private val privilegedToken = "0987654321"
  private val privilegedAuthRecord = AuthRecord(privilegedToken, None, testUsername, None, 3600, System.currentTimeMillis(), "", Some(true))

  val records = Map(
    nonPrivilegedToken -> nonPrivilegedAuthRecord,
    privilegedToken -> privilegedAuthRecord
  )
  val users = Map(testUsername -> testUser)


  "non-privileged user with right empref" should {
    "have access" in {
      val sut = new AuthorizedActionBuilder(empref1, new DummyAuthRecords(records), new DummyGatewayUsers(users), new SystemTimeSource)(ExecutionContext.global)
      sut.validateToken(nonPrivilegedToken).futureValue.value shouldBe nonPrivilegedAuthRecord
    }
  }

  "privileged user with other empref" should {
    "have access" in {
      val sut = new AuthorizedActionBuilder(empref2, new DummyAuthRecords(records), new DummyGatewayUsers(users), new SystemTimeSource)(ExecutionContext.global)
      sut.validateToken(privilegedToken).futureValue.value shouldBe privilegedAuthRecord
    }
  }
}

class DummyGatewayUsers(users: Map[String, GatewayUser]) extends StubGatewayUsers {
  override def forGatewayID(gatewayID: String)(implicit ec: ExecutionContext): Future[Option[GatewayUser]] =
    Future.successful(users.get(gatewayID))
}

class DummyAuthRecords(records: Map[String, AuthRecord]) extends StubAuthRecords {
  override def find(accessToken: String)(implicit ec: ExecutionContext): Future[Option[AuthRecord]] =
    Future.successful(records.get(accessToken))
}