package uk.gov.bis.levyApiMock.actions

import org.scalatest.{AsyncWordSpecLike, Matchers, OptionValues}
import uk.gov.bis.levyApiMock.data.oauth2.AuthRecord
import uk.gov.bis.levyApiMock.data.{GatewayUser, SystemTimeSource}

import scala.concurrent.{ExecutionContext, Future}

class AuthorizedActionBuilderTest extends AsyncWordSpecLike with Matchers with OptionValues {

  private val empref1 = "123/AB12345"
  private val empref2 = "321/XY12345"

  private val testUsername = "user"
  private val testUser = GatewayUser(testUsername, "", Some(empref1), None, None)

  private val nonPrivilegedToken = "12334567890"
  private val nonPrivilegedAuthRecord = AuthRecord(nonPrivilegedToken, None, None, testUsername, None, 3600, System.currentTimeMillis(), "", Some(false))
  private val privilegedToken = "0987654321"
  private val privilegedAuthRecord = AuthRecord(privilegedToken, None, None, testUsername, None, 3600, System.currentTimeMillis(), "", Some(true))

  val records = Map(
    nonPrivilegedToken -> nonPrivilegedAuthRecord,
    privilegedToken -> privilegedAuthRecord
  )
  val users = Map(testUsername -> testUser)


  "non-privileged user with right empref" should {
    "have access" in {
      val sut = new AuthorizedActionBuilder(empref1, new DummyAuthRecords(records), new DummyGatewayUsers(users), new SystemTimeSource)(ExecutionContext.global)
      sut.validateToken(nonPrivilegedToken).map(_.value shouldBe nonPrivilegedAuthRecord)
    }
  }

  "privileged user with other empref" should {
    "have access" in {
      val sut = new AuthorizedActionBuilder(empref2, new DummyAuthRecords(records), new DummyGatewayUsers(users), new SystemTimeSource)(ExecutionContext.global)
      sut.validateToken(privilegedToken).map(_.value shouldBe privilegedAuthRecord)
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