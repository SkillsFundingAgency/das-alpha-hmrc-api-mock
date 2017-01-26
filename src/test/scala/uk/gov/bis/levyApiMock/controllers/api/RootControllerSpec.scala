package uk.gov.bis.levyApiMock.controllers.api

import org.scalatest.{Matchers, OptionValues, WordSpecLike}
import uk.gov.bis.levyApiMock.actions.AuthenticatedAction
import uk.gov.bis.levyApiMock.data.levy.Href
import uk.gov.bis.levyApiMock.data.stubs.{StubAuthRecordOps, StubGatewayUserOps}
import views.html.helper.urlEncode

class RootControllerSpec extends WordSpecLike with Matchers with OptionValues {
  implicit val ec = scala.concurrent.ExecutionContext.Implicits.global
  val controller = new RootController(StubGatewayUserOps, new AuthenticatedAction(StubAuthRecordOps))
  "buildLinks" should {
    "build a map without an empref entry if empref is None" in {
      controller.buildLinks(None) shouldBe Map("self" -> Href("/"))
    }

    "build a map with an empref entry if empre is Some" in {
      val empref = "123/AB12345"
      controller.buildLinks(Some(empref)) shouldBe Map(
        "self" -> Href("/"),
        empref -> Href(s"/epaye/${urlEncode(empref)}")
      )
    }
  }

}
