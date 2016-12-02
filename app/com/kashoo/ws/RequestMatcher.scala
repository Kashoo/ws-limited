package com.kashoo.ws

import java.net.URI

import play.api.Configuration

/**
  * Defines a matcher for comparing against requests.
  *
  * @param host host to match against - required
  * @param port optional port to match against
  * @param path optional path to match against.  In the case that a request matches two matchers, the one with the longest path
  *             wins
  */
case class RequestMatcher(rate: Rate, host: String, port: Option[Int] = None, path: Option[String] = None)

object RequestMatcher {
  def apply(config: Configuration): RequestMatcher = {
    val host = config.getString("host").getOrElse(throw new IllegalStateException("Rate limit must include a host to match requests against"))
    val port = config.getInt("port")
    val path = config.getString("path")
    val rate = config.getString("rate").getOrElse(throw new IllegalStateException("Request matcher must specify a rate by which to limit matched requests"))
    RequestMatcher(Rate(config, rate), host, port, path)
  }

  /**
    * Matches the most specific matcher given a set of RequestMatchers and a URI
    * @param uri to match against
    * @param matchers to match with
    * @return an optional request matcher
    */
  def matchRequest(uri: URI, matchers: Set[RequestMatcher]): Option[RequestMatcher] =
    specificityMatchers.collectFirst {
      case matcher if matcher(UriWrapper(uri), matchers).isDefined => matcher(UriWrapper(uri), matchers).get
    }

  private val specificityMatchers: Seq[Matcher] = Seq(matchAll, matchHostAndPort, matchHost)

  private type Matcher = (UriWrapper, Set[RequestMatcher]) => Option[RequestMatcher]

  /**
    * Most specific matcher - matches on all fields.  When more than a single matcher are matched, the one with the longer path wins (more specific)
    */
  private def matchAll: Matcher = (uri, matchers) => matchers.collect {
    case rm if rm.host == uri.getHost && rm.port == uri.getPort && (rm.path == uri.getPath || pathsMatch(uri.getPath, rm.path)) =>
      rm
  } match {
    case matches if matches.isEmpty => None
    case matches => Some(matches.maxBy(_.path.get.length))
  }

  private def matchHostAndPort: Matcher = (uri, matchers) => matchers find { rm =>
    rm.host == uri.getHost && rm.port == uri.getPort
  }

  private def matchHost: Matcher = (uri, matchers) => matchers find {
    case RequestMatcher(_, host, None, None) if host == uri.getHost => true
    case _ => false
  }

  private def pathsMatch(uriPath: Option[String], matcherPath: Option[String]): Boolean = (uriPath, matcherPath) match {
    case (Some(uriPathString), Some(matcherPathString)) if uriPathString.contains(matcherPathString) => true
    case _ => false
  }

}

case class UriWrapper(uri: URI) {
  def getHost = uri.getHost
  def getPort: Option[Int] = if (uri.getPort == -1) None else Some(uri.getPort)
  def getPath = Option(uri.getPath)
}