package uk.gov.bis.levyApiMock.mongo

import javax.inject._

import play.modules.reactivemongo._
import reactivemongo.play.json.collection._
import uk.gov.bis.levyApiMock.data.levy.{LevyDeclarationOps, LevyDeclarationResponse}

import scala.concurrent.{ExecutionContext, Future}

class LevyDeclarationMongo @Inject()(val mongodb: ReactiveMongoApi) extends MongoCollection[LevyDeclarationResponse] with LevyDeclarationOps {

  def collectionF(implicit ec: ExecutionContext): Future[JSONCollection] = mongodb.database.map(_.collection[JSONCollection]("declarations"))

  override def byEmpref(empref: String)(implicit ec: ExecutionContext) = findOne("empref" -> empref)
}
