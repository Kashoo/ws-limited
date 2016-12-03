package com.kashoo.ws

import play.api.Configuration

/**
  * example config:
  *
  * com.kashoo.ws.rates = {
  *   rate1 = {
  *     requests = 20
  *     period = 1 second
  *   },
  *   rate2 = {
  *     requests = 5
  *     period = 1 second
  *   }
  * }
  * com.kashoo.ws.request-limits = [
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
case class RequestRateLimit(rate: Rate, requestMatcher: RequestMatcher) {
  // TODO: sort out ECs
  val ec = play.api.libs.concurrent.Execution.Implicits.defaultContext
  val rateLimit = RateLimit(rate)(ec)
}

object RequestRateLimit {
  def apply(rateConfig: Configuration, requestLimitConfig: Configuration): RequestRateLimit = {
    val rateName = requestLimitConfig.getString("rate").getOrElse(throw new IllegalStateException("Rate is required for a request limit configuration"))
    val rate = Rate(rateConfig, rateName)
    val reqMatcher = RequestMatcher(requestLimitConfig)
    RequestRateLimit(rate, reqMatcher)
  }
}