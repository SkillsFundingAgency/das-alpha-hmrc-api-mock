package db.levy

import javax.inject.Inject

import data.levy.{LevyDeclaration, LevyDeclarationOps}
import db.SlickModule
import play.api.db.slick.DatabaseConfigProvider

import scala.concurrent.{ExecutionContext, Future}

trait LevyDeclarationModule extends SlickModule {

  import driver.api._

  val LevyDeclarations = TableQuery[LevyDeclarationTable]

  class LevyDeclarationTable(tag: Tag) extends Table[LevyDeclaration](tag, "levy_declaration") {
    def year = column[Int]("year")

    def month = column[Int]("month")

    def amount = column[BigDecimal]("amount")

    def empref = column[String]("empref")

    def pk = primaryKey("levy_decl_pk", (year, month, empref))

    def * = (year, month, amount, empref) <>(LevyDeclaration.tupled, LevyDeclaration.unapply)
  }

}

class LevyDeclarations @Inject()(protected val dbConfigProvider: DatabaseConfigProvider) extends LevyDeclarationModule

class LevyDeclarationDAO @Inject()(levyDeclarations: LevyDeclarations) extends LevyDeclarationOps {

  import levyDeclarations._
  import levyDeclarations.api._

  def byEmpref(empref: String)(implicit ec: ExecutionContext): Future[Seq[LevyDeclaration]] = run(LevyDeclarations.filter(_.empref === empref).result)

  def insert(cat: LevyDeclaration)(implicit ec: ExecutionContext): Future[Unit] = run(LevyDeclarations += cat).map { _ => () }
}