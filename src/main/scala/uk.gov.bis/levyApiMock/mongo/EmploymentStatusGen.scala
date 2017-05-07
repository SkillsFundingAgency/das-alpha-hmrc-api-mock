package uk.gov.bis.levyApiMock.mongo

import javax.inject._

import cats.Functor
import cats.syntax.functor._
import cats.instances.future._
import org.joda.time.LocalDate
import play.modules.reactivemongo._
import uk.gov.bis.levyApiMock.controllers.api.EmploymentCheckResult
import uk.gov.bis.levyApiMock.data.levy.{EmploymentCheckRecord, EmploymentStatusOps}
import uk.gov.bis.levyApiMock.services.EmploymentStatusRepo

import scala.concurrent.{ExecutionContext, Future}

class EmploymentStatusMongo @Inject()(val mongodb: ReactiveMongoApi)(implicit ec: ExecutionContext) extends MongoCollection[EmploymentCheckRecord] with EmploymentStatusRepo[Future] {
  override val collectionName = "employment_check"

  override def find(empref: String, nino: String) = findMany("empref" -> empref, "nino" -> nino)
}

class EmploymentStatusGen[F[_] : Functor] @Inject()(repo: EmploymentStatusRepo[F]) extends EmploymentStatusOps[F] {
  override def employed(empref: String, nino: String, fromDate: LocalDate, toDate: LocalDate): F[Option[EmploymentCheckResult]] = {
    repo.find(empref, nino).map {
      case Seq() => None
      case results =>
        results.find(s => !fromDate.isBefore(s.fromDate) || !toDate.isAfter(s.toDate)) match {
          case None => Some(EmploymentCheckResult(empref, nino, fromDate, toDate, employed = false))
          case Some(ecr) => Some(EmploymentCheckResult(empref, nino, fromDate, toDate, employed = true))
        }
    }
  }
}

class EmploymentStatusImpl @Inject()(repo: EmploymentStatusRepo[Future])(implicit ec: ExecutionContext) extends EmploymentStatusGen[Future](repo)
