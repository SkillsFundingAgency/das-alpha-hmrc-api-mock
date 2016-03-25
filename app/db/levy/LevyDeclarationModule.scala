package db.levy

import javax.inject.{Inject, Singleton}

import db.DBModule
import play.api.db.slick.DatabaseConfigProvider

import scala.concurrent.{ExecutionContext, Future}

case class LevyDeclarationRow(year: Int, month: Int, amount: BigDecimal, empref: String)

trait LevyDeclarationModule extends DBModule {
  self: SchemeModule =>

  import driver.api._

  private val LevyDeclarations = TableQuery[LevyDeclarationTable]

  def byEmpref(empref: String): Future[Seq[LevyDeclarationRow]] = db.run(LevyDeclarations.filter(_.empref === empref).result)

  def insert(cat: LevyDeclarationRow): Future[Unit] = db.run(LevyDeclarations += cat).map { _ => () }

  class LevyDeclarationTable(tag: Tag) extends Table[LevyDeclarationRow](tag, "levy_declaration") {
    def year = column[Int]("year")

    def month = column[Int]("month")

    def amount = column[BigDecimal]("amount")

    def empref = column[String]("empref")

    def pk = primaryKey("levy_decl_pk", (year, month, empref))

    def schemeFK = foreignKey("decl_scheme_fk", empref, Schemes)(_.empref, onDelete = ForeignKeyAction.Cascade)

    def * = (year, month, amount, empref) <>(LevyDeclarationRow.tupled, LevyDeclarationRow.unapply)
  }

}

@Singleton
class LevyDeclarationDAO @Inject()(protected val dbConfigProvider: DatabaseConfigProvider)(implicit val ec: ExecutionContext)
  extends LevyDeclarationModule
    with SchemeModule