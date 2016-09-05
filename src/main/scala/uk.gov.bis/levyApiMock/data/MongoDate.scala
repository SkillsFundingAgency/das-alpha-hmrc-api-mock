package uk.gov.bis.levyApiMock.data

import org.joda.time.format.ISODateTimeFormat
import play.api.libs.json._

case class MongoDate(`$date`: String) {
  def longValue: Long = this
}

object MongoDate {

  import scala.language.implicitConversions

  val dtf = ISODateTimeFormat.dateTimeNoMillis()

  implicit def fromLong(ts: Long): MongoDate = MongoDate(dtf.print(ts))

  implicit def toLong(mongoDate: MongoDate): Long = dtf.parseDateTime(mongoDate.`$date`).getMillis

  implicit val fmt = Json.format[MongoDate]
}