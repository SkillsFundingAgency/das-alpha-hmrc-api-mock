package uk.gov.bis.levyApiMock.data.levy

import org.joda.time.{LocalDate, Months}

import scala.concurrent.{ExecutionContext, Future}

case class Fraction(region: String, value: BigDecimal)

case class FractionCalculation(calculatedAt: LocalDate, fractions: Seq[Fraction])

case class FractionResponse(empref: String, fractionCalculations: Seq[FractionCalculation])

trait FractionsOps {
  def byEmpref(empref: String)(implicit ec: ExecutionContext): Future[Option[FractionResponse]]
}

class DummyFractions extends FractionsOps {

  private val england: String = "England"

  val fractions = Seq(
    Fraction(england, 0.83),
    Fraction(england, 0.78),
    Fraction(england, 0.71)
  )

  override def byEmpref(empref: String)(implicit ec: ExecutionContext): Future[Option[FractionResponse]] = {

    val d = new LocalDate(2016, 2, 4)

    Future.successful {
      val fs = fractions.zipWithIndex.map { case (f, i) =>
        FractionCalculation(d.withPeriodAdded(Months.NINE, -i), List(f))
      }

      Some(FractionResponse(empref, fs))
    }
  }
}
