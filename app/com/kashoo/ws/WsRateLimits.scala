package com.kashoo.ws

import javax.inject.Inject

import play.api.Configuration
import play.api.libs.ws.{WSRequest, WSResponse}

import scala.concurrent.Future

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
  * com.kashoo.ws.api-limits = [
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
  * @param config
  */

class WsRateLimits @Inject() (config: Configuration) {

//  val definedLimits = readConfigDefinedLimits
//
//  def readConfigDefinedLimits = config.getConfigSeq("com.kashoo.ws.limits") match {
//    case Some(definedRates) => definedRates.map { definedRateLimitConfig =>
//      RequestLimiter(RequestMatcher(definedRateLimitConfig), RateLimit(definedRateLimitConfig))
//    }
//    case None => Seq.empty
//  }
//
//  def maybeLimit(request: WSRequest, exec: => Future[WSResponse]) = {
//    definedLimits.find(_.requestMatcher.check(request.uri)) match {
//      case Some(limiter) => limiter.rateLimit.limit(exec)
//      case None => exec
//    }
//  }
}
