package uk.gov.bis.levyApiMock.data.levy

import com.google.inject.ImplementedBy
import org.joda.time.LocalDate
import play.api.libs.json.Json
import uk.gov.bis.levyApiMock.controllers.api.EmploymentCheckResult
import uk.gov.bis.levyApiMock.mongo.EmploymentStatusGen

import scala.concurrent.Future

case class EmploymentCheckRecord(empref: String, nino: String, fromDate: LocalDate, toDate: LocalDate)

object EmploymentCheckRecord {
  implicit val format = Json.format[EmploymentCheckRecord]
}

@ImplementedBy(classOf[EmploymentStatusGen[Future]])
trait EmploymentStatusOps[F[_]] {
  def employed(empref: String, nino: String, fromDate: LocalDate, toDate: LocalDate): F[Option[EmploymentCheckResult]]
}

