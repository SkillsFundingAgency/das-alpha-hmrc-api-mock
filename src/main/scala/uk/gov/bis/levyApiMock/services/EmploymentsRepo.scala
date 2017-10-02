package uk.gov.bis.levyApiMock.services

import uk.gov.bis.levyApiMock.data.EmploymentCheckRecord

trait EmploymentsRepo[F[_]] {
  def find(empref: String, nino: String): F[Seq[EmploymentCheckRecord]]
}
