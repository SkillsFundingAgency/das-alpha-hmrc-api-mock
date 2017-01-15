package uk.gov.bis.levyApiMock.data

import org.apache.commons.codec.binary.Base32
import org.scalatest.{AsyncWordSpecLike, Matchers, OptionValues}
import uk.gov.bis.levyApiMock.auth.TOTP

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.{ExecutionContext, Future}

class ClientOpsSpec extends AsyncWordSpecLike with Matchers with OptionValues {
  val testTime = 9999L
  val timeSource = new TimeSource {
    override def currentTimeMillis() = testTime
  }

  val testClientOps = new TestClientOps(timeSource)

  "validate" should {
    "handle privileged access client" in {
      val token = TOTP.generateCodeAtTime(testClientOps.paSecret, 0L)
      testClientOps.validate(testClientOps.paClientID, Some(token.value), "").map(_ shouldBe true)
    }

    "handle non-privileged access client" in {
      testClientOps.validate(testClientOps.nonPaClientID, Some(testClientOps.nonPaSecret), "").map(_ shouldBe true)
    }
  }
}

class TestClientOps(val timeSource: TimeSource) extends ClientOps {
  val paClientID = "client-pa"
  val paSecret = new Base32().encodeToString("45122452-1da8-4b69-8f3d-651f42c698c6".getBytes)

  val nonPaClientID = "client-non-pa"
  val nonPaSecret = "45122452-1da8-4b69-8f3d-651f42c698c6"

  val applications = List(
    Application("app-pa", "app-id-1", paClientID, paSecret, "server-token-1", privilegedAccess = true),
    Application("app-non-pa", "app-id-2", nonPaClientID, nonPaSecret, "server-token-2", privilegedAccess = false)
  )

  override def forId(clientID: String)(implicit ec: ExecutionContext): Future[Option[Application]] = Future.successful {
    applications.find(_.clientID == clientID)
  }
}