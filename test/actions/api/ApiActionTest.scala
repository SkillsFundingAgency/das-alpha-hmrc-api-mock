package actions.api

import db.levy.GatewayIdSchemeOps
import db.outh2.{AuthRecordOps, AuthRecordRow}
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.{FlatSpec, Matchers}

import scala.concurrent.Future

class ApiActionTest extends FlatSpec with Matchers with ScalaFutures {

  import TestData._

  "validateToken" should "return a list of emprefs" in {
    testAction.validateToken(validToken).futureValue shouldBe Right(testEmprefs)
  }

  it should "return an error" in {
    testAction.validateToken(invalidToken).futureValue shouldBe a[Left[_, _]]
  }
}

object TestData {
  implicit val ec = scala.concurrent.ExecutionContext.Implicits.global

  lazy val testAction = new ApiAction(authRecords, enrolments)

  val validToken = "abc"

  val invalidToken = "xyz"

  val testEmprefs = List("123/AB12345")


  val authRecords = new AuthRecordOps {
    override def all(): Future[Seq[AuthRecordRow]] = ???

    override def clearExpired(): Future[Unit] = ???

    override def find(accessToken: String): Future[Option[AuthRecordRow]] = Future.successful {
      if (accessToken == validToken) Some(AuthRecordRow(accessToken, "read:test", "gateway1", "client1", System.currentTimeMillis() + 1000, System.currentTimeMillis() - 1000))
      else None
    }

    override def create(token: AuthRecordRow): Future[Unit] = ???
  }

  val enrolments = new GatewayIdSchemeOps {
    override def bindEmprefs(gatewayId: String, emprefs: List[String]): Future[Unit] = ???

    override def emprefsForId(gatewayId: String): Future[Seq[String]] = Future.successful {
      if (gatewayId == "gateway1") testEmprefs else Seq()
    }
  }

}
