package actions.api

import java.security.cert.X509Certificate

import db.levy.GatewayIdSchemeOps
import db.outh2.{AuthRecordOps, AuthRecordRow}
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.{FlatSpec, Matchers}
import play.api.mvc.Results._
import play.api.mvc.{AnyContentAsEmpty, Request, RequestHeader, Result}
import play.api.test.FakeRequest

import scala.concurrent.Future

class ApiActionTest extends FlatSpec with Matchers with ScalaFutures {

  import TestData._

  "validateToken" should "return a list of emprefs" in {
    testAction.validateToken(validToken).futureValue shouldBe Right(testEmprefs)
  }

  it should "return an error" in {
    testAction.validateToken(invalidToken).futureValue shouldBe a[Left[_, _]]
  }

  import TestRequests._

  "refine" should "return an Unauthorized when there is no Authorization header" in {
    val result: Either[Result, ApiRequest[AnyContentAsEmpty.type]] = testAction.refine(requestWithoutAuthorization).futureValue
    result shouldBe a[Left[_, _]]
    result.left.toOption.map(_.header.status) shouldBe Some(401)
  }

  it should "return an Unauthorized when the Authorization header is not Bearer" in {
    val result: Either[Result, ApiRequest[AnyContentAsEmpty.type]] = testAction.refine(requestWithBasicAuth).futureValue
    result shouldBe a[Left[_, _]]
    result.left.toOption.map(_.header.status) shouldBe Some(401)
  }

  it should "return an Unauthorized when the Authorization header is a non-matching Bearer token" in {
    val result: Either[Result, ApiRequest[AnyContentAsEmpty.type]] = testAction.refine(requestWithNonMatchingBearer).futureValue
    result shouldBe a[Left[_, _]]
    result.left.toOption.map(_.header.status) shouldBe Some(401)
  }

  it should "return an ApiRequest when the Authorization header is a matching Bearer token" in {
    val result: Either[Result, ApiRequest[AnyContentAsEmpty.type]] = testAction.refine(requestWithMatchingBearer).futureValue
    result shouldBe a[Right[_, _]]
  }
}

object TestRequests {

  import TestData._

  val requestWithoutAuthorization = FakeRequest()
  val requestWithBasicAuth = FakeRequest().withHeaders("Authorization" -> "Basic abcdefg")
  val requestWithMatchingBearer = FakeRequest().withHeaders("Authorization" -> s"Bearer $validToken")
  val requestWithNonMatchingBearer = FakeRequest().withHeaders("Authorization" -> s"Bearer $invalidToken")
}

object TestData {
  implicit val ec = scala.concurrent.ExecutionContext.Implicits.global

  lazy val testAction = new ApiAction(authRecords, enrolments)

  val validToken = "abc"

  val invalidToken = "xyz"

  val testEmprefs = List("123/AB12345")


  val authRecords = new AuthRecordOps {
    override def all(): Future[Seq[AuthRecordRow]] = Future.successful(Seq())

    override def clearExpired(): Future[Unit] = Future.successful(())

    override def find(accessToken: String): Future[Option[AuthRecordRow]] = Future.successful {
      if (accessToken == validToken) Some(AuthRecordRow(accessToken, "read:test", "gateway1", "client1", System.currentTimeMillis() + 1000, System.currentTimeMillis() - 1000))
      else None
    }

    override def create(token: AuthRecordRow): Future[Unit] = Future.successful(())
  }

  val enrolments = new GatewayIdSchemeOps {
    override def bindEmprefs(gatewayId: String, emprefs: List[String]): Future[Unit] = Future.successful(())

    override def emprefsForId(gatewayId: String): Future[Seq[String]] = Future.successful {
      if (gatewayId == "gateway1") testEmprefs else Seq()
    }
  }

}
