package com.kashoo.ws

import java.util.concurrent.TimeUnit

import org.mockito.Mockito._
import org.scalatest.{FlatSpec, Matchers}
import org.scalatestplus.mockito.MockitoSugar
import play.api.Configuration

import scala.concurrent.duration._

class RateSpec extends FlatSpec with Matchers with MockitoSugar {

  trait TestScope {
    val mockRateConfig = mock[Configuration]
    val mockInnerRateConfig = mock[Configuration]
  }

  "RateLimit" should "instantiate from a full, valid configuration" in new TestScope() {
    when(mockRateConfig.getOptional[Configuration]("rate1")).thenReturn(Some(mockInnerRateConfig))
    when(mockInnerRateConfig.getOptional[Int]("queries")).thenReturn(Some(5))
    when(mockInnerRateConfig.getOptional[String]("period")).thenReturn(Some("5 seconds"))

    val rateLimit = Rate(mockRateConfig, "rate1")
    rateLimit.number shouldBe 5
    rateLimit.period shouldBe Duration(5, TimeUnit.SECONDS)

    verify(mockRateConfig).getOptional[Configuration]("rate1")
    verify(mockInnerRateConfig).getOptional[Int]("queries")
    verify(mockInnerRateConfig).getOptional[String]("period")
  }

  it should "throw an exception when instantiated with a rate name configuration that does not exist" in new TestScope() {
    when(mockRateConfig.getOptional[Configuration]("rate1")).thenReturn(None)

    intercept[IllegalStateException] {
      Rate(mockRateConfig, "rate1")
    }.getMessage shouldBe "Could not find rate configuration for rate1 ( com.kashoo.ws.rates.rate1 )"
  }

  it should "throw an exception when instantiated with a configuration without a queries value" in new TestScope() {
    when(mockRateConfig.getOptional[Configuration]("rate1")).thenReturn(Some(mockInnerRateConfig))
    when(mockInnerRateConfig.getOptional[Int]("queries")).thenReturn(None)
    when(mockInnerRateConfig.getOptional[String]("period")).thenReturn(Some("5 seconds"))

    intercept[IllegalStateException] {
      Rate(mockRateConfig, "rate1")
    }.getMessage shouldBe "Rate limit must define the number of queries"
  }

  it should "throw an exception when instantiated with a configuration without a period value" in new TestScope() {
    when(mockRateConfig.getOptional[Configuration]("rate1")).thenReturn(Some(mockInnerRateConfig))
    when(mockInnerRateConfig.getOptional[Int]("queries")).thenReturn(Some(10))
    when(mockInnerRateConfig.getOptional[String]("period")).thenReturn(None)

    intercept[IllegalStateException] {
      Rate(mockRateConfig, "rate1")
    }.getMessage shouldBe "Rate limit must have a period"
  }
}
