package uk.gov.bis.levyApiMock.data.levy

import uk.gov.bis.levyApiMock.models.LevyDeclarations

import scala.concurrent.{ExecutionContext, Future}

case class LevyDeclarationData(year: Int, month: Int, amount: BigDecimal, empref: String, submissionType: String, submissionDate: String)

trait LevyDeclarationOps {
  def byEmpref(empref: String)(implicit ec: ExecutionContext): Future[Option[LevyDeclarations]]
}
