package uk.gov.bis.levyApiMock.data.levy

import org.joda.time.LocalDate

import scala.concurrent.{ExecutionContext, Future}

case class Fraction(region: String, value: BigDecimal)

case class FractionCalculation(calculatedAt: LocalDate, fractions: List[Fraction])

trait FractionsOps {
  def byEmpref(empref: String)(implicit ec: ExecutionContext): Future[Seq[FractionCalculation]]
}
