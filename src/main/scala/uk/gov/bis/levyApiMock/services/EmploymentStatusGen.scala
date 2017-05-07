package uk.gov.bis.levyApiMock.services

import javax.inject.Inject

import cats.Functor
import cats.instances.future._
import cats.syntax.functor._
import uk.gov.bis.levyApiMock.controllers.ClosedDateRange
import uk.gov.bis.levyApiMock.controllers.api.EmploymentCheckResult
import uk.gov.bis.levyApiMock.data.levy.EmploymentStatusOps

import scala.concurrent.{ExecutionContext, Future}

class EmploymentStatusGen[F[_] : Functor] @Inject()(repo: EmploymentStatusRepo[F]) extends EmploymentStatusOps[F] {
  override def employed(empref: String, nino: String, dateRange: ClosedDateRange): F[Option[EmploymentCheckResult]] = {
    repo.find(empref, nino).map {
      case Seq() => None
      case results =>
        results.find(s => dateRange.overlaps(ClosedDateRange(s.fromDate, s.toDate))) match {
          case None => Some(EmploymentCheckResult(empref, nino, dateRange.from, dateRange.to, employed = false))
          case Some(ecr) => Some(EmploymentCheckResult(empref, nino, dateRange.from, dateRange.to, employed = true))
        }
    }
  }
}


class EmploymentStatusImpl @Inject()(repo: EmploymentStatusRepo[Future])(implicit ec: ExecutionContext)
  extends EmploymentStatusGen[Future](repo)(catsStdInstancesForFuture)