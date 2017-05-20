package uk.gov.bis.levyApiMock.controllers

import org.joda.time.LocalDate
import org.scalatest.{Matchers, WordSpecLike}

class ClosedDateRangeTest extends WordSpecLike with Matchers {

  val refRange = ClosedDateRange(new LocalDate(2017, 1, 1), new LocalDate(2017, 1, 31))

  "overlaps" should {
    "return correct result" in {
      refRange.overlaps(ClosedDateRange(new LocalDate(2017, 1, 1), new LocalDate(2017, 1, 31))) shouldBe true
      refRange.overlaps(ClosedDateRange(new LocalDate(2016, 12, 1), new LocalDate(2016, 12, 31))) shouldBe false
      refRange.overlaps(ClosedDateRange(new LocalDate(2016, 12, 1), new LocalDate(2017, 1, 1))) shouldBe true
      refRange.overlaps(ClosedDateRange(new LocalDate(2017, 1, 31), new LocalDate(2017, 2, 10))) shouldBe true
      refRange.overlaps(ClosedDateRange(new LocalDate(2017, 1, 5), new LocalDate(2017, 1, 10))) shouldBe true
      refRange.overlaps(ClosedDateRange(new LocalDate(2016, 12, 1), new LocalDate(2017, 2, 20))) shouldBe true

      refRange.overlaps(ClosedDateRange(new LocalDate(2017, 2, 1), new LocalDate(2017, 2, 10))) shouldBe false
    }
  }

}
