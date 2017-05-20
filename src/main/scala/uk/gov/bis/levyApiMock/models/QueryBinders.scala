package uk.gov.bis.levyApiMock.models

import org.joda.time._
import org.joda.time.format.DateTimeFormatterBuilder
import play.api.mvc.QueryStringBindable

import scala.util.Try

trait DateConverter {

  //yyyy-MM-dd
  lazy val dateFormatter = new DateTimeFormatterBuilder()
    .appendFixedDecimal(DateTimeFieldType.year, 4)
    .appendLiteral("-")
    .appendFixedDecimal(DateTimeFieldType.monthOfYear, 2)
    .appendLiteral("-")
    .appendFixedDecimal(DateTimeFieldType.dayOfMonth, 2)
    .toFormatter

  final def parseToLong(date: String): Long = dateFormatter.withZoneUTC().parseMillis(date)

  final def parseToDateTime(date: String): DateTime = dateFormatter.withZoneUTC().parseDateTime(date).toDateTime(DateTimeZone.UTC)

  final def parseToLocalDate(date: String): LocalDate = dateFormatter.parseLocalDate(date)

  final def formatToString(date: Long): String = new DateTime(date, DateTimeZone.UTC).toString(dateFormatter)

  final def formatToString(date: DateTime): String = date.toString(dateFormatter)

  final def formatToString(date: LocalDate): String = date.toString(dateFormatter)

  final def safeParse[A](f: => A)(t: => Throwable): A = try f catch {
    case e: IllegalArgumentException => throw t
  }
}

object DateConverter extends DateConverter

object QueryBinders {

  implicit def bindableLocalDate(implicit stringBinder: QueryStringBindable[String]) = new QueryStringBindable[LocalDate] {

    def bind(key: String, params: Map[String, Seq[String]]): Option[Either[String, LocalDate]] = {
      params.get(key).flatMap(_.headOption).map { date: String => (Try {
        Right(DateConverter.parseToLocalDate(date))
      } recover {
        case e: Exception => Left("date parameter is in the wrong format. Should be (yyyy-MM-dd)")
      }).get
      }
    }

    def unbind(key: String, value: LocalDate): String = QueryStringBindable.bindableString.unbind(key, DateConverter.formatToString(value))
  }
}
