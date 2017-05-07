package uk.gov.bis.levyApiMock.services

import org.joda.time.LocalDate
import org.scalatest.{Matchers, OptionValues, WordSpecLike}
import uk.gov.bis.levyApiMock.controllers.ClosedDateRange
import uk.gov.bis.levyApiMock.data.levy.EmploymentCheckRecord

class EmploymentStatusGenTest extends WordSpecLike with Matchers with OptionValues {

  import EmploymentStatusGenTestSupport._

  val sut = new EmploymentStatusGen[TestF](repo)

  "employed" should {
    "return no records" in {
      sut.employed("foo", "bar", ClosedDateRange(new LocalDate(), new LocalDate()))(TestData(Seq.empty))._2 shouldBe None
    }

    val empStart = new LocalDate(2017, 1, 1)
    val empEnd = new LocalDate(2017, 2, 28)
    val data = TestData(Seq(EmploymentCheckRecord("foo", "bar", empStart, empEnd)))

    "return 'employed' if the request specifies a range that overlaps with the employment in any way" in {
      sut.employed("foo", "bar", ClosedDateRange(empStart.minusDays(1), empEnd.plusDays(1)))(data)._2.value.employed shouldBe true
      sut.employed("foo", "bar", ClosedDateRange(empStart, empEnd))(data)._2.value.employed shouldBe true
      sut.employed("foo", "bar", ClosedDateRange(empStart, empEnd.minusDays(1)))(data)._2.value.employed shouldBe true
    }
  }
}
