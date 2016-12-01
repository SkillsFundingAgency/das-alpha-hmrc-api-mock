package uk.gov.bis.levyApiMock

import scala.util.{Failure, Success}

case class Config(taxservice: TaxServiceConfig)

case class TaxServiceConfig(baseURI: String)

object Config {

  import pureconfig._

  lazy val config = loadConfig[Config] match {
    case Success(c) => c
    case Failure(t) => throw t
  }
}
