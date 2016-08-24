package uk.gov.bis.levyApiMock.mongo

import javax.inject._

import play.api.libs.json.Json
import play.modules.reactivemongo._
import uk.gov.bis.levyApiMock.data.levy.{Fraction, FractionCalculation, FractionResponse, FractionsOps}

import scala.concurrent.ExecutionContext

class FractionMongo @Inject()(val mongodb: ReactiveMongoApi) extends MongoCollection[FractionResponse] with FractionsOps {

  implicit val fractionR = Json.reads[Fraction]
  implicit val fractionCalcR = Json.reads[FractionCalculation]
  implicit val fractionRepsonseR = Json.reads[FractionResponse]

  override val collectionName = "fractions"

  override def byEmpref(empref: String)(implicit ec: ExecutionContext) = findOne("empref" -> empref)

}
