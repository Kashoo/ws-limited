package com.kashoo.ws

import play.api.{Configuration, Logger}

import scala.concurrent.ExecutionContext

/**
  * example config:
  *
  * ws.limited.rates = {
  *   rate1 = {
  *     requests = 20
  *     period = 1 second
  *   },
  *   rate2 = {
  *     requests = 5
  *     period = 1 second
  *   }
  * }
  * ws.limited.policies = [
  *  {
  *    rate = rate1
  *    host = "squareapi.com"
  *    port = 9000
  *  }
  *  {
  *    rate = rate2
  *    host = "squareapi.com"
  *    port = 9000
  *  }
  * ]
  *
  */
case class RequestRateLimit(rate: Rate, requestMatcher: RequestMatcher)(implicit val ec: ExecutionContext) {
  val rateLimit: RateLimit = RateLimit(rate)(ec)
}

object RequestRateLimit {

  val logger = Logger("request-rate-limit")

  def apply(rateConfig: Configuration, requestLimitConfig: Configuration)
           (implicit ec: ExecutionContext = play.api.libs.concurrent.Execution.Implicits.defaultContext): RequestRateLimit = {
    val rateName = requestLimitConfig.getString("rate").getOrElse(throw new IllegalStateException("Rate is required for a request limit configuration"))
    val rate = Rate(rateConfig, rateName)
    val reqMatcher = RequestMatcher(requestLimitConfig)
    logger.trace(s"Enabling client request rate limit against $reqMatcher with $rate, using $ec")
    RequestRateLimit(rate, reqMatcher)(ec)
  }
}