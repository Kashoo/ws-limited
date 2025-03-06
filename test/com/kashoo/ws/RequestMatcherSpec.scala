package com.kashoo.ws

import org.mockito.Mockito._
import play.api.Configuration
import org.specs2.mock.Mockito
import org.specs2.specification.Scope
import play.api.test.PlaySpecification

class RequestMatcherSpec extends PlaySpecification with Mockito {

  class TestScope extends Scope {
    val mockConfig: Configuration = mock[Configuration]

    def fullVerify: Option[String] = {
      verify(mockConfig).getOptional[String]("host")
      verify(mockConfig).getOptional[Int]("port")
      verify(mockConfig).getOptional[String]("path")
    }
  }

  "RequestMatcher" should {
    "instantiate from a full, valid configuration" in new TestScope {
      when(mockConfig.getOptional[String]("host")).thenReturn(Some("example.com"))
      when(mockConfig.getOptional[Int]("port")).thenReturn(Some(9001))
      when(mockConfig.getOptional[String]("path")).thenReturn(Some("/somepath/somewhere"))

      val matcher = RequestMatcher(mockConfig)

      matcher.host must be_==("example.com")
      matcher.port must beSome(9001)
      matcher.path must beSome("/somepath/somewhere")

      fullVerify
    }

    "instantiate from a configuration without a port" in new TestScope {
      when(mockConfig.getOptional[String]("host")).thenReturn(Some("example.com"))
      when(mockConfig.getOptional[Int]("port")).thenReturn(None)
      when(mockConfig.getOptional[String]("path")).thenReturn(Some("/somepath/somewhere"))

      val matcher = RequestMatcher(mockConfig)

      matcher.host must be_==("example.com")
      matcher.port must beNone
      matcher.path must beSome("/somepath/somewhere")

      fullVerify
    }

    "instantiate from a configuration without a path" in new TestScope {
      when(mockConfig.getOptional[String]("host")).thenReturn(Some("example.com"))
      when(mockConfig.getOptional[Int]("port")).thenReturn(Some(9001))
      when(mockConfig.getOptional[String]("path")).thenReturn(None)

      val matcher = RequestMatcher(mockConfig)

      matcher.host must be_==("example.com")
      matcher.port must beSome(9001)
      matcher.path must beNone

      fullVerify
    }

    "not instantiate from a configuration without a host" in new TestScope {
      when(mockConfig.getOptional[String]("host")).thenReturn(None)
      when(mockConfig.getOptional[Int]("port")).thenReturn(Some(9001))
      when(mockConfig.getOptional[String]("path")).thenReturn(Some("/somepath/somewhere"))

      RequestMatcher(mockConfig) must throwA[IllegalStateException]
    }
  }
}
