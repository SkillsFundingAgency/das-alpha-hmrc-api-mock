package uk.gov.bis.filters

import javax.inject.Inject

import akka.stream.Materializer
import play.api.Logger
import play.api.mvc._
import uk.gov.bis.utils.TimeSource

import scala.concurrent.{ExecutionContext, Future}

class LoggingFilter @Inject()(timeSource: TimeSource)(implicit val mat: Materializer, ec: ExecutionContext) extends Filter {

  def apply(nextFilter: RequestHeader => Future[Result])
           (requestHeader: RequestHeader): Future[Result] = {

    val startTime = timeSource.currentTimeMillis()

    nextFilter(requestHeader).map { result =>

      val endTime = timeSource.currentTimeMillis()
      val requestTime = endTime - startTime

      Logger.info(s"${requestHeader.method} ${requestHeader.uri} took ${requestTime}ms and returned ${result.header.status}")

      result.withHeaders("Request-Time" -> requestTime.toString)
    }
  }
}