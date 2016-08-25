package uk.gov.bis.levyApiMock

import scala.util.{Failure, Success}

case class TaxServiceConfig(baseURI:String)

object TaxServiceConfig {

  import pureconfig._

  lazy val config = loadConfig[TaxServiceConfig] match {
    case Success(c) => c
    case Failure(t) => throw t
  }
}
