package uk.gov.bis.levyApiMock.data.levy

import scala.concurrent.{ExecutionContext, Future}

case class EmployerName(nameLine1: Option[String], nameLine2: Option[String])

case class Employer(name: EmployerName)

case class EmprefResponse(_links: Map[String, Href], empref: String, employer: Employer)

trait EmprefOps {
  def forEmpref(empref: String)(implicit ec: ExecutionContext): Future[Option[EmprefResponse]]
}


