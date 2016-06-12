package uk.gov.bis.levyApiMock.data.levy

import scala.concurrent.{ExecutionContext, Future}

case class LevyDeclarationData(year: Int, month: Int, amount: BigDecimal, empref: String, submissionType: String, submissionDate: String)

trait LevyDeclarationOps {
  def byEmpref(empref: String)(implicit ec: ExecutionContext): Future[Seq[LevyDeclarationData]]

  def insert(decl: LevyDeclarationData)(implicit ec: ExecutionContext): Future[Unit]

  def insert(decls: Seq[LevyDeclarationData])(implicit ec: ExecutionContext): Future[Unit]

  def replaceForEmpref(empref:String, decls:Seq[LevyDeclarationData])(implicit ec: ExecutionContext): Future[(Int, Int)]
}
