package actions.api

import org.scalatest.concurrent.ScalaFutures
import org.scalatest.{FlatSpec, Matchers}
import play.api.mvc.{Request, Result, Results}

import scala.concurrent.Future

class AuthorizedActionTest extends FlatSpec with Matchers with ScalaFutures with Results {

  import TestData._

  "validateToken" should "return true" in {
    val ab = testAction("empref", "123/AB12345", "read:test")
    ab.validateToken(validToken, "empref", "123/AB12345", "read:test").futureValue shouldBe true
  }

  it should "return an error" in {
    testAction("", "", "").validateToken(invalidToken, "", "", "").futureValue shouldBe false
  }

  import TestRequests._

  def badRequest[A](request: Request[A]): Future[Result] = Future.successful(BadRequest(""))

  def ok[A](request: Request[A]): Future[Result] = Future.successful(Ok(""))

  "invokeBlock" should "return an Unauthorized when there is no Authorization header" in {
    val result: Result = testAction("empref", "", "").invokeBlock(requestWithoutAuthorization, badRequest).futureValue
    result.header.status shouldBe 401
  }

  it should "return an Unauthorized when the Authorization header is not Bearer" in {
    val result = testAction("", "", "").invokeBlock(requestWithBasicAuth, badRequest).futureValue
    result.header.status shouldBe 401
  }

  it should "return an Unauthorized when the Authorization header is a non-matching Bearer token" in {
    val result = testAction("", "", "").invokeBlock(requestWithNonMatchingBearer, badRequest).futureValue
    result.header.status shouldBe 401
  }

  it should "return an ApiRequest when the Authorization header is a matching Bearer token" in {
    val result = testAction("empref", "abc", "read:test").invokeBlock(requestWithMatchingBearer, ok).futureValue
    result.header.status shouldBe 200
  }
}




