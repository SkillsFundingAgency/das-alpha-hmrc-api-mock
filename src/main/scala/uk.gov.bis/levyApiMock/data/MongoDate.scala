package uk.gov.bis.levyApiMock.data

import play.api.libs.json.Json

case class MongoDate(`$numberLong`: String) {
  def longValue: Long = this
}

object MongoDate {

  import scala.language.implicitConversions

  def apply(ts: Long): MongoDate = MongoDate(ts.toString)

  implicit def fromLong(ts: Long): MongoDate = MongoDate(ts)

  implicit def toLong(mongoDate: MongoDate): Long = mongoDate.`$numberLong`.toLong

  implicit val fmt = Json.format[MongoDate]
}