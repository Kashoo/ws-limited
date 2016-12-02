package com.kashoo.ws

import com.google.common.util.concurrent.RateLimiter
import play.api.Configuration

import scala.concurrent._
import scala.concurrent.duration._

/**
  * Simple rate limiter
  * Inspired from https://github.com/wspringer/scala-rate-limiter
  */
case class Rate(number: Int, period: Duration) {
  override def toString: String = s"$number requests per $period"
  val requestPeriod: Duration = period / number
}

object Rate {

  implicit class RateBuilder(val number: Int) extends AnyVal {
    def per(duration: Duration) = Rate(number, duration)
    def / (duration: Duration) = Rate(number, duration)
  }

  def apply(config: Configuration, rateName: String): Rate = {
    val rateConfig = config.getConfig("com.kashoo.ws.rates").getOrElse(throw new IllegalArgumentException("Rates config could not be found (under key 'com.kashoo.ws.rates')"))
    val rateNameConfig = config.getConfig(rateName).getOrElse(throw new IllegalStateException(s"Could not find rate configuration for $rateName ( com.kashoo.ws.rates.$rateName )"))
    val queries = rateNameConfig.getInt("queries").getOrElse(throw new IllegalStateException("Rate limit must define the number of queries"))
    val period = rateNameConfig.getString("period").getOrElse(throw new IllegalStateException("Rate limit must have a period"))
    Rate(queries, Duration(period))
  }

}

case class RateLimit(rate: Rate) {
  val limiter = RateLimiter.create(rate.number / (rate.period / 1.second))

  /**
    * Limits the provided function by a delay if necessary
    */
  def limit[T](fn: => Future[T])(implicit ec: ExecutionContext) = {
    Future {
      blocking {
        limiter.acquire()
      }
    }(ec) flatMap { permit =>
      fn
    }
  }
}
