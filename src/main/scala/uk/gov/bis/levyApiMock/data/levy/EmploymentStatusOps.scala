package uk.gov.bis.levyApiMock.data.levy

import org.joda.time.LocalDate
import play.api.libs.json.Json
import uk.gov.bis.levyApiMock.controllers.ClosedDateRange
import uk.gov.bis.levyApiMock.controllers.api.EmploymentCheckResult

case class EmploymentCheckRecord(empref: String, nino: String, fromDate: LocalDate, toDate: LocalDate)

object EmploymentCheckRecord {
  implicit val format = Json.format[EmploymentCheckRecord]
}

trait EmploymentStatusOps[F[_]] {
  def employed(empref: String, nino: String, dateRange: ClosedDateRange): F[Option[EmploymentCheckResult]]
}

