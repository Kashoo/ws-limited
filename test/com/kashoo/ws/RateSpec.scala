package com.kashoo.ws

import java.util.concurrent.TimeUnit
import org.mockito.Mockito._
import org.specs2.mock.Mockito
import org.specs2.specification.Scope
import play.api.Configuration
import play.api.test.PlaySpecification

import scala.concurrent.duration._

class RateSpec extends PlaySpecification with Mockito {

  class TestScope extends Scope {
    val mockRateConfig: Configuration = mock[Configuration]
    val mockInnerRateConfig: Configuration = mock[Configuration]
  }

  "RateLimit" should {
    "instantiate from a full, valid configuration" in new TestScope() {
      when(mockRateConfig.getOptional[Configuration]("rate1")).thenReturn(Some(mockInnerRateConfig))
      when(mockInnerRateConfig.getOptional[Int]("queries")).thenReturn(Some(5))
      when(mockInnerRateConfig.getOptional[String]("period")).thenReturn(Some("5 seconds"))

      val rateLimit: Rate = Rate(mockRateConfig, "rate1")
      rateLimit.number mustEqual  5
      rateLimit.period mustEqual  Duration(5, TimeUnit.SECONDS)

      verify(mockRateConfig).getOptional[Configuration]("rate1")
      verify(mockInnerRateConfig).getOptional[Int]("queries")
      verify(mockInnerRateConfig).getOptional[String]("period")
    }

    "throw an exception when instantiated with a rate name configuration that does not exist" in new TestScope() {
      when(mockRateConfig.getOptional[Configuration]("rate1")).thenReturn(None)

      Rate(mockRateConfig, "rate1") must throwA[IllegalStateException].like {
        case e => e.getMessage must be_==("Could not find rate configuration for rate1 ( com.kashoo.ws.rates.rate1 )")
      }
    }

    "throw an exception when instantiated with a configuration without a queries value" in new TestScope() {
      when(mockRateConfig.getOptional[Configuration]("rate1")).thenReturn(Some(mockInnerRateConfig))
      when(mockInnerRateConfig.getOptional[Int]("queries")).thenReturn(None)
      when(mockInnerRateConfig.getOptional[String]("period")).thenReturn(Some("5 seconds"))

      Rate(mockRateConfig, "rate1") must throwA[IllegalStateException].like {
        case e => e.getMessage must be_==("Rate limit must define the number of queries")
      }
    }

    "throw an exception when instantiated with a configuration without a period value" in new TestScope() {
      when(mockRateConfig.getOptional[Configuration]("rate1")).thenReturn(Some(mockInnerRateConfig))
      when(mockInnerRateConfig.getOptional[Int]("queries")).thenReturn(Some(10))
      when(mockInnerRateConfig.getOptional[String]("period")).thenReturn(None)

      Rate(mockRateConfig, "rate1") must throwA[IllegalStateException].like {
        case e => e.getMessage must be_==("Rate limit must have a period")
      }
    }
  }
}
