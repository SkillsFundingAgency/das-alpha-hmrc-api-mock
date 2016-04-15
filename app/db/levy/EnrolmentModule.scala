package db.levy

import javax.inject.Inject

import data.levy.{Enrolment, EnrolmentOps, ServiceBinding}
import db.SlickModule
import play.api.db.slick.DatabaseConfigProvider
import slick.lifted.PrimaryKey

import scala.concurrent.{ExecutionContext, Future}

trait EnrolmentModule extends SlickModule {

  import driver.api._

  val Enrolments = TableQuery[EnrolmentTable]

  class EnrolmentTable(tag: Tag) extends Table[Enrolment](tag, "enrolment") {
    def gatewayId: Rep[String] = column[String]("gateway_id")

    def identifierType: Rep[String] = column[String]("identifier_type")

    def taxId: Rep[String] = column[String]("tax_id")

    def pk: PrimaryKey = primaryKey("enrolment_pk", (gatewayId, taxId))

    def * = (gatewayId, identifierType, taxId) <>(Enrolment.tupled, Enrolment.unapply)
  }

}

class Enrolments @Inject()(protected val dbConfigProvider: DatabaseConfigProvider) extends EnrolmentModule

class EnrolmentDAO @Inject()(protected val gatewayIdSchemes: Enrolments)
  extends EnrolmentOps {

  import gatewayIdSchemes._
  import api._

  def enrolmentsForGatewayId(gatewayId: String)(implicit ec: ExecutionContext): Future[Seq[ServiceBinding]] = run {
    val q = for {
      e <- Enrolments if e.gatewayId === gatewayId
    } yield (e.identifierType, e.taxId)

    q.result.map(_.map(ServiceBinding.tupled))
  }

  /**
    * Replace existing list of emprefs held for the gatewayId with the new list
    */
  def bindEnrolments(gatewayId: String, enrolments: List[ServiceBinding])(implicit ec: ExecutionContext): Future[Unit] = run {
    for {
      _ <- Enrolments.filter(_.gatewayId === gatewayId).delete
      _ <- Enrolments ++= enrolments.map(e => Enrolment(gatewayId, e.identifierType, e.taxId))
    } yield ()
  }

}
