package uk.gov.bis.levyApiMock.data

import scala.concurrent.{ExecutionContext, Future}


trait LevyDeclarationOps {
  def byEmpref(empref: String)(implicit ec: ExecutionContext): Future[Option[LevyDeclarationResponse]]
}
