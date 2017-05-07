package uk.gov.bis.levyApiMock.services

import com.google.inject.ImplementedBy
import uk.gov.bis.levyApiMock.data.levy.EmploymentCheckRecord
import uk.gov.bis.levyApiMock.mongo.EmploymentStatusMongo

trait EmploymentStatusRepo[F[_]] {
  def find(empref: String, nino: String): F[Seq[EmploymentCheckRecord]]
}
