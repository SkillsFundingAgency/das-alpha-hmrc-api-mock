package uk.gov.bis.levyApiMock.data.levy

import org.joda.time.{LocalDate, Months}

import scala.concurrent.{ExecutionContext, Future}

case class Fraction(region: String, value: BigDecimal)

case class FractionCalculation(calculatedAt: LocalDate, fractions: List[Fraction])

trait FractionsOps {
  def byEmpref(empref: String)(implicit ec: ExecutionContext): Future[Seq[FractionCalculation]]
}


class DummyFractions extends FractionsOps {

  private val england: String = "England"

  val fractions = Seq(
    Fraction(england, 0.83),
    Fraction(england, 0.78),
    Fraction(england, 0.71)
  )

  override def byEmpref(empref: String)(implicit ec: ExecutionContext): Future[Seq[FractionCalculation]] = {

    val d = new LocalDate(2016, 2, 4)

    Future.successful {
      fractions.zipWithIndex.map { case (f, i) =>
        FractionCalculation(d.withPeriodAdded(Months.NINE, -i), List(f))
      }
    }
  }
}
