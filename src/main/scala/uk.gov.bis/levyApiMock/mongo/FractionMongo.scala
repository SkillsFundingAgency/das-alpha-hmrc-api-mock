package uk.gov.bis.levyApiMock.mongo

import javax.inject._

import org.joda.time.LocalDate
import play.api.libs.json.{Json, Reads}
import play.modules.reactivemongo._
import uk.gov.bis.levyApiMock.data.levy._

import scala.concurrent.{ExecutionContext, Future}

class FractionMongo @Inject()(val mongodb: ReactiveMongoApi) extends MongoCollection[FractionResponse] with FractionsOps {

  implicit val fractionR = Json.reads[Fraction]
  implicit val fractionCalcR = Json.reads[FractionCalculation]
  implicit val fractionRepsonseR = Json.reads[FractionResponse]

  override val collectionName = "fractions"

  override def byEmpref(empref: String)(implicit ec: ExecutionContext) = findOne("empref" -> empref)

}

class FractionCalcMongo @Inject()(val mongodb: ReactiveMongoApi) extends MongoCollection[FractionCalculationDate] with FractionCalcOps {
  override def collectionName: String = "fraction_calculation_date"

  implicit val jldReads = Reads.jodaLocalDateReads("yyyy-MM-dd")
  implicit val fcReads = Json.reads[FractionCalculationDate]

  override def lastCalculationDate(implicit ec: ExecutionContext): Future[Option[LocalDate]] = {
    findOne().map(_.map(_.lastCalculationDate))
  }
}
