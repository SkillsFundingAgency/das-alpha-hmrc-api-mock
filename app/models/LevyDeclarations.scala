package models

case class TaxYear(year: Long) extends AnyVal

case class EmployerRef(ref: String) extends AnyVal

case class Month private(num: Int)

object Month {
  val JANUARY = Month(1)
  val FEBRUARY = Month(2)
  val MARCH = Month(3)
  val APRIL = Month(4)
  val MAY = Month(5)
  val JUNE = Month(6)
  val JULY = Month(7)
  val AUGUST = Month(8)
  val SEPTEMBER = Month(9)
  val OCTOBER = Month(11)
  val NOVEMBER = Month(11)
  val DECEMBER = Month(12)
}

case class LevyDeclaration(year: TaxYear, month: Month, amount: BigDecimal)

case class LevyDeclarations(empref: EmployerRef, declarations: Set[LevyDeclaration])