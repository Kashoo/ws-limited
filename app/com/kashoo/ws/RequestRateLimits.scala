package com.kashoo.ws

import java.net.URI

import play.api.Configuration
import play.libs.Akka

object RequestRateLimits {

  type RequestRateLimits = Set[RequestRateLimit]

  def apply(rrl: RequestRateLimit*): RequestRateLimits = Set[RequestRateLimit](rrl:_*)

  def apply(config: Configuration): RequestRateLimits = {
    val rateLimiterEc = config.getConfig("ws.limited.execution-context") match {
      case Some(ecConfig) => Akka.system.dispatchers.lookup("ws.limited.execution-context")
      case None => play.api.libs.concurrent.Execution.Implicits.defaultContext
    }
    val rateConfig = config.getConfig("ws.limited.rates").getOrElse(throw new IllegalStateException("Could not find configuration for WS rate limits (ws.limited.rates)"))
    val reqMatcherConfigs = config.getConfigSeq("ws.limited.policies").getOrElse(throw new IllegalStateException("Could not find configuration for WS request rate limits (ws.limited.policies)"))
    reqMatcherConfigs.map(RequestRateLimit(rateConfig, _)(rateLimiterEc)).toSet
  }

  /**
    * Matches the most specific matcher given a set of RequestMatchers and a URI
    *
    * @param uri    to match against
    * @param limits to match with
    * @return an optional request matcher match
    */
  def matchRequest(uri: URI, limits: RequestRateLimits): Option[RequestRateLimit] =
    specificityMatchers.collectFirst {
      case matcher if matcher(UriWrapper(uri), limits).isDefined => matcher(UriWrapper(uri), limits).get
    }

  private val specificityMatchers: Seq[Matcher] = Seq(matchAll, matchHostAndPort, matchHost)

  private type Matcher = (UriWrapper, RequestRateLimits) => Option[RequestRateLimit]

  /**
    * Most specific matcher - matches on all fields.  When more than a single matcher are matched, the one with the longer (more specific) path wins
    */
  private def matchAll: Matcher = (uri, rateLimits) => rateLimits.collect {
    case rl if rl.requestMatcher.host == uri.getHost && rl.requestMatcher.port == uri.getPort && (rl.requestMatcher.path == uri.getPath || pathsMatch(uri.getPath, rl.requestMatcher.path)) => rl
  } match {
    case matches if matches.isEmpty => None
    case matches => Some(matches.maxBy(_.requestMatcher.path.get.length))
  }

  private def matchHostAndPort: Matcher = (uri, rateLimits) => rateLimits find { _.requestMatcher match {
    case RequestMatcher(host, port, None) if host == uri.getHost && port == uri.getPort => true
    case _ => false
  }}

  private def matchHost: Matcher = (uri, rateLimits) => rateLimits find { _.requestMatcher match {
    case RequestMatcher(host, None, None) if host == uri.getHost => true
    case _ => false
  }}

  private def pathsMatch(uriPath: Option[String], matcherPath: Option[String]): Boolean = (uriPath, matcherPath) match {
    case (Some(uriPathString), Some(matcherPathString)) if uriPathString.contains(matcherPathString) => true
    case _ => false
  }

  private case class UriWrapper(uri: URI) {
    def getHost: String = uri.getHost
    def getPort: Option[Int] = if (uri.getPort == -1) None else Some(uri.getPort)
    def getPath = Option(uri.getPath)
  }
}