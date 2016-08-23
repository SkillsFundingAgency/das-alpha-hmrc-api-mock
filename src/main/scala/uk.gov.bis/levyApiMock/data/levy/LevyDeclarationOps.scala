package uk.gov.bis.levyApiMock.data.levy

import uk.gov.bis.levyApiMock.models.LevyDeclarationResponse

import scala.concurrent.{ExecutionContext, Future}


trait LevyDeclarationOps {
  def byEmpref(empref: String)(implicit ec: ExecutionContext): Future[Option[LevyDeclarationResponse]]
}
