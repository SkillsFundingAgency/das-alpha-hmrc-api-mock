package uk.gov.bis.oauth.auth

import play.api.Logger
import uk.gov.bis.levyApiMock.Config

object OAuthTrace {
  def apply(s: String): Unit = if (Config.config.traceOAuthRequests.getOrElse(false)) Logger.debug(s) else Unit
}
