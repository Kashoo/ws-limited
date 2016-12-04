package com.kashoo.ws

import org.scalatest.mock.MockitoSugar
import org.scalatest.{Matchers, FlatSpec}
import play.api.Configuration
import org.mockito.Mockito._

class RequestRateLimitSpec extends FlatSpec with Matchers with MockitoSugar {

  trait TestScope {
    val mockRateConfig = mock[Configuration]
    val mockInnerRateConfig = mock[Configuration]
    when(mockRateConfig.getConfig("rate1")).thenReturn(Some(mockInnerRateConfig))
    when(mockInnerRateConfig.getInt("queries")).thenReturn(Some(5))
    when(mockInnerRateConfig.getString("period")).thenReturn(Some("5 seconds"))

    val mockRateLimitConfig = mock[Configuration]
    when(mockRateLimitConfig.getString("host")).thenReturn(Some("example.com"))

    def fullVerify = {
      verify(mockRateLimitConfig).getString("rate", null)
      verify(mockRateLimitConfig).getString("host", null)
      verify(mockRateLimitConfig).getInt("port")
      verify(mockRateLimitConfig).getString("path", null)
    }
  }

  "RequestRateLimit" should "instantiate from a full, valid configuration" in new TestScope() {
    when(mockRateLimitConfig.getString("rate")).thenReturn(Some("rate1"))

    RequestRateLimit(mockRateConfig, mockRateLimitConfig)

    fullVerify
  }

  it should "throw an exception when instantiated from a configuration without a rate" in new TestScope() {
    when(mockRateLimitConfig.getString("rate")).thenReturn(None)

    intercept[IllegalStateException] {
      RequestRateLimit(mockRateConfig, mockRateLimitConfig)
    }
  }

}