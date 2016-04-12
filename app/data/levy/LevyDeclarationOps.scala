package data.levy

import scala.concurrent.Future

case class LevyDeclaration(year: Int, month: Int, amount: BigDecimal, empref: String)

trait LevyDeclarationOps {
  def byEmpref(empref: String): Future[Seq[LevyDeclaration]]

  def insert(cat: LevyDeclaration): Future[Unit]
}
