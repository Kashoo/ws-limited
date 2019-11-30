package com.kashoo.ws

import com.google.common.util.concurrent.RateLimiter
import com.typesafe.config.ConfigObject
import play.api.Configuration

import scala.concurrent._
import scala.concurrent.duration._

/**
  * Simple Rate definition
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

  def apply(rateConfig: Configuration, rateName: String): Rate = {
    val rateNameConfig = rateConfig.getOptional[Configuration](rateName).getOrElse(throw new IllegalStateException(s"Could not find rate configuration for $rateName ( com.kashoo.ws.rates.$rateName )"))
    val queries = rateNameConfig.getOptional[Int]("queries").getOrElse(throw new IllegalStateException("Rate limit must define the number of queries"))
    val period = rateNameConfig.getOptional[String]("period").getOrElse(throw new IllegalStateException("Rate limit must have a period"))
    Rate(queries, Duration(period))
  }

}

/**
  * Rudimentary rate limiter using Guava's RateLimiter
  * Inspired from https://github.com/wspringer/scala-rate-limiter
  * @param rate rate to limit invocations at
  * @param ec thread pool to execute limited tasks on
  */
case class RateLimit(rate: Rate)(implicit ec: ExecutionContext) {
  private val limiter: RateLimiter = RateLimiter.create(rate.number / (rate.period / 1.second))

  /**
    * Limits the provided function by a delay if necessary
    */
  def limit[T](fn: => Future[T]): Future[T] = {
    Future {
      blocking {
        limiter.acquire()
      }
    }(ec) flatMap { permit =>
      fn
    }
  }
}
