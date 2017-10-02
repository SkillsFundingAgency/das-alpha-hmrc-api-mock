package uk.gov.bis.levyApiMock.data

import org.joda.time.LocalDate

import scala.concurrent.{ExecutionContext, Future}

case class Fraction(region: String, value: BigDecimal)

case class FractionCalculation(calculatedAt: LocalDate, fractions: Seq[Fraction])

case class FractionResponse(empref: String, fractionCalculations: Seq[FractionCalculation])

case class FractionCalculationDate(lastCalculationDate: LocalDate)

trait FractionsOps {
  def byEmpref(empref: String)(implicit ec: ExecutionContext): Future[Option[FractionResponse]]

}

trait FractionCalcOps {
  def lastCalculationDate(implicit ec: ExecutionContext): Future[Option[LocalDate]]
}
