package data.levy

import scala.concurrent.{ExecutionContext, Future}

case class LevyDeclaration(year: Int, month: Int, amount: BigDecimal, empref: String)

trait LevyDeclarationOps {
  def byEmpref(empref: String)(implicit ec: ExecutionContext): Future[Seq[LevyDeclaration]]

  def insert(cat: LevyDeclaration)(implicit ec: ExecutionContext): Future[Unit]
}
