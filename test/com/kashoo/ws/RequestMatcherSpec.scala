package com.kashoo.ws

import org.mockito.Mockito._
import org.scalatest.{FlatSpec, Matchers}
import org.scalatestplus.mockito.MockitoSugar
import play.api.Configuration

class RequestMatcherSpec extends FlatSpec with Matchers with MockitoSugar {

  trait TestScope {
    val mockConfig = mock[Configuration]

    def fullVerify = {
      verify(mockConfig).getOptional[String]("host")
      verify(mockConfig).getOptional[Int]("port")
      verify(mockConfig).getOptional[String]("path")
    }
  }

  "RequestMatcher" should "instantiate from a full, valid configuration" in new TestScope() {
    when(mockConfig.getOptional[String]("host")).thenReturn(Some("example.com"))
    when(mockConfig.getOptional[Int]("port")).thenReturn(Some(9001))
    when(mockConfig.getOptional[String]("path")).thenReturn(Some("/somepath/somewhere"))

    val matcher = RequestMatcher(mockConfig)

    matcher.host shouldBe "example.com"
    matcher.port shouldBe Some(9001)
    matcher.path shouldBe Some("/somepath/somewhere")

    fullVerify
  }

  it should "instantiate from a configuration without a port" in new TestScope() {
    when(mockConfig.getOptional[String]("host")).thenReturn(Some("example.com"))
    when(mockConfig.getOptional[Int]("port")).thenReturn(None)
    when(mockConfig.getOptional[String]("path")).thenReturn(Some("/somepath/somewhere"))

    val matcher = RequestMatcher(mockConfig)

    matcher.host shouldBe "example.com"
    matcher.port shouldBe None
    matcher.path shouldBe Some("/somepath/somewhere")

    fullVerify
  }

  it should "instantiate from a configuration without a path" in new TestScope() {
    when(mockConfig.getOptional[String]("host")).thenReturn(Some("example.com"))
    when(mockConfig.getOptional[Int]("port")).thenReturn(Some(9001))
    when(mockConfig.getOptional[String]("path")).thenReturn(None)

    val matcher = RequestMatcher(mockConfig)

    matcher.host shouldBe "example.com"
    matcher.port shouldBe Some(9001)
    matcher.path shouldBe None

    fullVerify
  }

  it should "not instantiate from a configuration without a host" in new TestScope() {
    when(mockConfig.getOptional[String]("host")).thenReturn(None)
    when(mockConfig.getOptional[Int]("port")).thenReturn(Some(9001))
    when(mockConfig.getOptional[String]("path")).thenReturn(Some("/somepath/somewhere"))

    intercept[IllegalStateException] {
      RequestMatcher(mockConfig)
    }
  }

}
