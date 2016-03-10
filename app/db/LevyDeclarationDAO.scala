package db

import javax.inject.Inject

import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import slick.driver.JdbcProfile

import scala.concurrent.Future

case class LevyDeclarationRow(year: Int, month: Int, amount: BigDecimal, empref: String)

class LevyDeclarationDAO @Inject()(protected val dbConfigProvider: DatabaseConfigProvider, val schemeDAO: SchemeDAO) extends HasDatabaseConfigProvider[JdbcProfile] {

  import driver.api._

  private val LevyDeclarations = TableQuery[LevyDeclarationTable]

  def all(): Future[Seq[LevyDeclarationRow]] = db.run(LevyDeclarations.result)

  def byEmpref(empref: String): Future[Seq[LevyDeclarationRow]] = db.run(LevyDeclarations.filter(_.empref === empref).result)

  def insert(cat: LevyDeclarationRow): Future[Unit] = db.run(LevyDeclarations += cat).map { _ => () }

  class LevyDeclarationTable(tag: Tag) extends Table[LevyDeclarationRow](tag, "LEVY_DECLARATION") {
    def year = column[Int]("YEAR")

    def month = column[Int]("MONTH")

    def amount = column[BigDecimal]("AMOUNT")

    def empref = column[String]("EMPREF")

    def pk = primaryKey("levy_decl_pk", (year, month, empref))

    def schemeFK = foreignKey("decl_scheme_fk", empref, schemeDAO.Schemes)(_.empref, onDelete = ForeignKeyAction.Cascade)

    def * = (year, month, amount, empref) <>(LevyDeclarationRow.tupled, LevyDeclarationRow.unapply)
  }

}