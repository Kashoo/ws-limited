package com.kashoo.ws

import java.net.URI

import com.kashoo.ws.Rate._
import org.specs2.mock.Mockito
import org.specs2.specification.Scope
import play.api.test.PlaySpecification

import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global

class RequestRateLimitsSpec extends PlaySpecification with Mockito {

  class TestScope extends Scope {
    val basicRate: Rate = 5 per 1.second

    val mostSpecific: RequestMatcher = RequestMatcher("example.com", Some(9001), Some("some/path/with/several/segments"))
    val nextSpecific: RequestMatcher = RequestMatcher("example.com", Some(9001), Some("some/path"))
    val specificWithoutPort: RequestMatcher = RequestMatcher("example.com", None, Some("some/path/with/several/segments"))
    val shorterPathSpecificWithoutPort: RequestMatcher = RequestMatcher("example.com", None, Some("some/path"))
    val lessSpecific: RequestMatcher = RequestMatcher("example.com", Some(9001))
    val leastSpecific: RequestMatcher = RequestMatcher("example.com")

    val matchers: Set[RequestMatcher] = Set(leastSpecific, lessSpecific, nextSpecific, mostSpecific, specificWithoutPort, shorterPathSpecificWithoutPort)

    val requestRateLimits: Set[RequestRateLimit] = matchers.map(RequestRateLimit(basicRate, _))

    def assertMatch(testUri: URI, expectedMatcher: RequestMatcher): org.specs2.execute.Result = {
      val result = RequestRateLimits.matchRequest(testUri, requestRateLimits)
      result must beSome
      result.get.requestMatcher must be_==(expectedMatcher)
    }
  }

  "RequestRateLimits" should {
    "match the most specific matcher for a URI with matching host, port, and all path segments" in new TestScope {
      assertMatch(new URI("http://example.com:9001/some/path/with/several/segments/endpoint"), mostSpecific)
    }

    "match the most specific matcher for a URI with matching host, port, and all path segments with a trailing slash" in new TestScope {
      assertMatch(new URI("http://example.com:9001/some/path/with/several/segments/endpoint/"), mostSpecific)
    }

    "match the most specific matcher with a shorter path for a URI with matching host, port and shorter path segment" in new TestScope {
      assertMatch(new URI("http://example.com:9001/some/path/with/different/segments"), nextSpecific)
    }

    "match a specific matcher without a port for a URI with matching host and all path segments" in new TestScope {
      assertMatch(new URI("http://example.com/some/path/with/several/segments/endpoint/"), specificWithoutPort)
    }

    "match a specific matcher without a port for a URI with matching host and a shorter path segment" in new TestScope {
      assertMatch(new URI("http://example.com/some/path/with/several/slugs"), shorterPathSpecificWithoutPort)
    }

    "match the less specific matcher for a URI matching only host and port" in new TestScope {
      assertMatch(new URI("http://example.com:9001/some/completely/different/path"), lessSpecific)
    }

    "match the least specific matcher for a URI matching only host" in new TestScope {
      assertMatch(new URI("http://example.com:9002/some/completely/different/path"), leastSpecific)
    }

    "not find a match for a mismatching URI" in new TestScope {
      val result = RequestRateLimits.matchRequest(new URI("http://some-other-example.com/some/path"), requestRateLimits)
      result must beNone
    }
  }
}
