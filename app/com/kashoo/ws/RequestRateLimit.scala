package com.kashoo.ws

import play.api.{Configuration, Logger}

import scala.concurrent.ExecutionContext

/**
  * Associates a rate limit with a request matcher for applying to outgoing client requests.
  */
case class RequestRateLimit(rate: Rate, requestMatcher: RequestMatcher)(implicit val ec: ExecutionContext) {
  val rateLimit: RateLimit = RateLimit(rate)(ec)
}

object RequestRateLimit {

  val logger = Logger("request-rate-limit")

  def apply(rateConfig: Configuration, requestLimitConfig: Configuration)
           (implicit ec: ExecutionContext): RequestRateLimit = {
    val rateName = requestLimitConfig.getOptional[String]("rate").getOrElse(throw new IllegalStateException("Rate is required for a request limit configuration"))
    val rate = Rate(rateConfig, rateName)
    val reqMatcher = RequestMatcher(requestLimitConfig)
    logger.trace(s"Enabling client request rate limit against $reqMatcher with $rate, using $ec")
    RequestRateLimit(rate, reqMatcher)(ec)
  }
}