package com.kashoo.ws

import org.scalatest.{FlatSpec, Matchers}
import play.api.Configuration
import org.mockito.Mockito._
import org.scalatestplus.mockito.MockitoSugar
import scala.concurrent.ExecutionContext.Implicits.global

class RequestRateLimitSpec extends FlatSpec with Matchers with MockitoSugar {

  trait TestScope {
    val mockRateConfig = mock[Configuration]
    val mockInnerRateConfig = mock[Configuration]
    when(mockRateConfig.getOptional[Configuration]("rate1")).thenReturn(Some(mockInnerRateConfig))
    when(mockInnerRateConfig.getOptional[Int]("queries")).thenReturn(Some(5))
    when(mockInnerRateConfig.getOptional[String]("period")).thenReturn(Some("5 seconds"))

    val mockRateLimitConfig = mock[Configuration]
    when(mockRateLimitConfig.getOptional[String]("host")).thenReturn(Some("example.com"))

    def fullVerify = {
      verify(mockRateLimitConfig).getOptional[String]("rate")
      verify(mockRateLimitConfig).getOptional[String]("host")
      verify(mockRateLimitConfig).getOptional[Int]("port")
      verify(mockRateLimitConfig).getOptional[String]("path")
    }
  }

  "RequestRateLimit" should "instantiate from a full, valid configuration" in new TestScope() {
    when(mockRateLimitConfig.getOptional[String]("rate")).thenReturn(Some("rate1"))

    RequestRateLimit(mockRateConfig, mockRateLimitConfig)

    fullVerify
  }

  it should "throw an exception when instantiated from a configuration without a rate" in new TestScope() {
    when(mockRateLimitConfig.getOptional[String]("rate")).thenReturn(None)

    intercept[IllegalStateException] {
      RequestRateLimit(mockRateConfig, mockRateLimitConfig)
    }
  }

}