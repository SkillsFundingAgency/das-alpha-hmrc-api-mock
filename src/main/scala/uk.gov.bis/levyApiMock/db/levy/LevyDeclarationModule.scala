package uk.gov.bis.levyApiMock.db.levy

import javax.inject.Inject

import uk.gov.bis.levyApiMock.data.levy.{LevyDeclaration, LevyDeclarationOps}
import uk.gov.bis.levyApiMock.db.SlickModule

import scala.concurrent.{ExecutionContext, Future}

trait LevyDeclarationModule extends SlickModule {

  import driver.api._

  val LevyDeclarations = TableQuery[LevyDeclarationTable]

  class LevyDeclarationTable(tag: Tag) extends Table[LevyDeclaration](tag, "levy_declaration") {
    def year = column[Int]("year")

    def month = column[Int]("month")

    def submissionType = column[String]("submission_type")

    def submissionDate = column[String]("submission_date")

    def amount = column[BigDecimal]("amount")

    def empref = column[String]("empref")

    def * = (year, month, amount, empref, submissionType, submissionDate) <>(LevyDeclaration.tupled, LevyDeclaration.unapply)
  }

}

class LevyDeclarationDAO @Inject()(levyDeclarations: LevyDeclarationModule) extends LevyDeclarationOps {

  import levyDeclarations._
  import levyDeclarations.api._

  override def byEmpref(empref: String)(implicit ec: ExecutionContext): Future[Seq[LevyDeclaration]] = run(LevyDeclarations.filter(_.empref === empref).result)

  override def insert(decl: LevyDeclaration)(implicit ec: ExecutionContext): Future[Unit] = run(LevyDeclarations += decl).map { _ => () }

  override def insert(decls: Seq[LevyDeclaration])(implicit ec: ExecutionContext): Future[Unit] = run(LevyDeclarations ++= decls).map(_ => ())

  override def replaceForEmpref(empref: String, decls: Seq[LevyDeclaration])(implicit ec: ExecutionContext): Future[(Int, Int)] = run {
    for {
      deleteCount <- LevyDeclarations.filter(_.empref === empref).delete
      insertCount <- LevyDeclarations ++= decls
    } yield (deleteCount, insertCount.getOrElse(0))
  }
}