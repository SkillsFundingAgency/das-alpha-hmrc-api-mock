package uk.gov.bis.levyApiMock.services

import javax.inject.Inject

import cats.Functor
import cats.instances.future._
import cats.syntax.functor._
import uk.gov.bis.levyApiMock.controllers.{ClosedDateRange, EmploymentCheckResult}
import uk.gov.bis.levyApiMock.data.EmploymentStatusOps

import scala.concurrent.{ExecutionContext, Future}

class EmploymentsGen[F[_] : Functor] @Inject()(repo: EmploymentsRepo[F]) extends EmploymentStatusOps[F] {
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

class EmploymentsImpl @Inject()(repo: EmploymentsRepo[Future])(implicit ec: ExecutionContext)
  extends EmploymentsGen[Future](repo)(catsStdInstancesForFuture)