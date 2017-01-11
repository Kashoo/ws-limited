package com.kashoo.ws

import java.net.URI

import com.kashoo.ws.Rate._
import org.scalatest._

import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global

class RequestRateLimitsSpec extends FlatSpec with Matchers {

  trait TestScope {
    val basicRate = 5 per 1.second

    val mostSpecific = RequestMatcher("example.com", Some(9001), Some("some/path/with/several/segments"))
    val nextSpecific = RequestMatcher("example.com", Some(9001), Some("some/path"))
    val specificWithoutPort = RequestMatcher("example.com", None, Some("some/path/with/several/segments"))
    val shorterPathSpecificWithoutPort = RequestMatcher("example.com", None, Some("some/path"))
    val lessSpecific = RequestMatcher("example.com", Some(9001))
    val leastSpecific = RequestMatcher("example.com")

    val matchers = Set(leastSpecific, lessSpecific, nextSpecific, mostSpecific, specificWithoutPort, shorterPathSpecificWithoutPort)

    val requestRateLimits = matchers.map(RequestRateLimit(basicRate, _))

    def assertMatch(testUri: URI, expectedMatcher: RequestMatcher) = {
      val result = RequestRateLimits.matchRequest(testUri, requestRateLimits)
      result shouldBe defined
      result.get.requestMatcher shouldBe expectedMatcher
    }
  }

  "RequestRateLimits" should "match the most specific matcher for a URI with matching host, port, and all path segments" in new TestScope {
    assertMatch(new URI("http://example.com:9001/some/path/with/several/segments/endpoint"), mostSpecific)
  }

  it should "match the most specific matcher for a URI with matching host, port, and all path segments with a trailing slash" in new TestScope {
    assertMatch(new URI("http://example.com:9001/some/path/with/several/segments/endpoint/"), mostSpecific)
  }

  it should "match the most specific matcher with a shorter path for a URI with matching host, port and shorter path segment" in new TestScope {
    assertMatch(new URI("http://example.com:9001/some/path/with/different/segments"), nextSpecific)
  }

  it should "match a specific matcher without a port for a URI with matching host and all path segments" in new TestScope {
    assertMatch(new URI("http://example.com/some/path/with/several/segments/endpoint/"), specificWithoutPort)
  }

  it should "match a specific matcher without a port for a URI with matching host and a shorter path segment" in new TestScope {
    assertMatch(new URI("http://example.com/some/path/with/several/slugs"), shorterPathSpecificWithoutPort)
  }

  it should "match the less specific matcher for a URI matching only host and port" in new TestScope {
    assertMatch(new URI("http://example.com:9001/some/completely/different/path"), lessSpecific)
  }

  it should "match the least specific matcher for a URI matching only host" in new TestScope {
    assertMatch(new URI("http://example.com:9002/some/completely/different/path"), leastSpecific)
  }

  it should "not find a match for a mismatching URI" in new TestScope {
    RequestRateLimits.matchRequest(new URI("http://some-other-example.com/some/path"), requestRateLimits) shouldBe None
  }

}
