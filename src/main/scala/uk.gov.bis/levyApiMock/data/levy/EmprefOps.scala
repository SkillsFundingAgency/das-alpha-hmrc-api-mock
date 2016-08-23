package uk.gov.bis.levyApiMock.data.levy

import uk.gov.bis.levyApiMock.mongo.EmprefResponse

import scala.concurrent.{ExecutionContext, Future}

trait EmprefOps {
  def forEmpref(empref: String)(implicit ec: ExecutionContext): Future[Option[EmprefResponse]]
}
