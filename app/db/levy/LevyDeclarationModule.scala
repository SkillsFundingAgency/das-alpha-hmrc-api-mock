package db.levy

import javax.inject.Inject

import data.levy.{LevyDeclarationOps, LevyDeclaration}
import db.DBModule
import play.api.db.slick.DatabaseConfigProvider

import scala.concurrent.{ExecutionContext, Future}

trait LevyDeclarationModule extends DBModule {

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

class LevyDeclarationDAO @Inject()(protected val dbConfigProvider: DatabaseConfigProvider)(implicit val ec: ExecutionContext)
  extends LevyDeclarationModule with LevyDeclarationOps {

  import driver.api._

  def byEmpref(empref: String): Future[Seq[LevyDeclaration]] = db.run(LevyDeclarations.filter(_.empref === empref).result)

  def insert(cat: LevyDeclaration): Future[Unit] = db.run(LevyDeclarations += cat).map { _ => () }
}