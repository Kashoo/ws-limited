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
case class RequestMatcher(host: String, port: Option[Int] = None, path: Option[String] = None)

object RequestMatcher {
  def apply(config: Configuration): RequestMatcher = {
    val host = config.getString("host").getOrElse(throw new IllegalStateException("Rate limit must include a host to match requests against"))
    val port = config.getInt("port")
    val path = config.getString("path")
    RequestMatcher(host, port, path)
  }
}