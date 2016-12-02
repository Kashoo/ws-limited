package com.kashoo.ws

import java.net.URI

import play.api.Configuration

/**
  * Defines a matcher for comparing against requests.
  *
  * @param host
  * @param port
  * @param path
  */
case class RequestMatcher(rate: Rate, host: String, port: Option[Int] = None, path: Option[String] = None) {

  def check(uri: URI) = {
    def hostMatch = uri.getHost.equals(host)
    def portMatch = uri.getPort.equals(port.getOrElse(uri.getPort))
    def pathMatch = uri.getPath.equals(path.getOrElse(uri.getPath))
    hostMatch && portMatch && pathMatch
  }
}

object RequestMatcher {
  def apply(config: Configuration): RequestMatcher = {
    val host = config.getString("host").getOrElse(throw new IllegalStateException("Rate limit must include a host to match requests against"))
    val port = config.getInt("port")
    val path = config.getString("path")
    val rate = config.getString("rate").getOrElse(throw new IllegalStateException("Request matcher must specify a rate by which to limit matched requests"))
    RequestMatcher(Rate(config, rate), host, port, path)
  }

  def matchRequest(uri: URI, matchers: Set[RequestMatcher]): Option[RequestMatcher] = {
    specificityMatchers.find {
      case matcher if matcher(UriWrapper(uri), matchers).isDefined => true
      case _ => false
    } flatMap { matcher =>
      specificityMatchers
      matcher(UriWrapper(uri), matchers)
    }
  }

  private val specificityMatchers: Seq[Matcher] = Seq(matchAll, matchHostAndPort, matchHost)

  private type Matcher = (UriWrapper, Set[RequestMatcher]) => Option[RequestMatcher]

  private def matchAll: Matcher = (uri, matchers) => matchers find { rm =>
    rm.host == uri.getHost && rm.port == uri.getPort && (rm.path == uri.getPath || pathsMatch(uri.getPath, rm.path))
  }

  private def matchHostAndPort: Matcher = (uri, matchers) => matchers find { rm =>
    rm.host == uri.getHost && rm.port == uri.getPort
  }

  private def matchHost: Matcher = (uri, matchers) => matchers find { rm =>
    rm.host == uri.getHost
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