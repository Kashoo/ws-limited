package com.kashoo.ws

import play.api.Configuration

/**
  * Defines a matcher for comparing against requests.
  *
  * @param host host to match against - required
  * @param port optional port to match against
  * @param path optional path to match against.  In the case that a request matches two matchers, the one with the longest path
  *             wins
  */
case class RequestMatcher(host: String, port: Option[Int] = None, path: Option[String] = None) {
  override def toString: String = s"$host${port.map(p => s":$p").getOrElse("")}${path.map(p => s"/$p").getOrElse("")}"
}

object RequestMatcher {
  def apply(config: Configuration): RequestMatcher = {
    val host = config.getOptional[String]("host").getOrElse(throw new IllegalStateException("Rate limit must include a host to match requests against"))
    val port = config.getOptional[Int]("port")
    val path = config.getOptional[String]("path")
    RequestMatcher(host, port, path)
  }
}