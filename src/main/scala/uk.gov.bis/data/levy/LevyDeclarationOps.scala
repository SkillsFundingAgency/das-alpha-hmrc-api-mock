package uk.gov.bis.data.levy

import scala.concurrent.{ExecutionContext, Future}

case class LevyDeclaration(year: Int, month: Int, amount: BigDecimal, empref: String, submissionType: String, submissionDate: String)

trait LevyDeclarationOps {
  def byEmpref(empref: String)(implicit ec: ExecutionContext): Future[Seq[LevyDeclaration]]

  def insert(decl: LevyDeclaration)(implicit ec: ExecutionContext): Future[Unit]

  def insert(decls: Seq[LevyDeclaration])(implicit ec: ExecutionContext): Future[Unit]

  def replaceForEmpref(empref:String, decls:Seq[LevyDeclaration])(implicit ec: ExecutionContext): Future[(Int, Int)]
}
