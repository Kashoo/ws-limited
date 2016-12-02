package com.kashoo.ws

import java.net.URI

import com.kashoo.ws.Rate._
import org.scalatest._

import scala.concurrent.duration._

class RequestMatcherSpec extends FlatSpec with Matchers {

  trait TestScope {
    val basicRate = 5 per 1.second

    val mostSpecific = RequestMatcher(basicRate, "example.com", Some(9001), Some("some/path/with/several/segments"))
    val nextSpecific = RequestMatcher(basicRate, "example.com", Some(9001), Some("some/path"))
    val specificWithoutPort = RequestMatcher(basicRate, "example.com", None, Some("some/path/with/several/segments"))
    val shorterPathSpecificWithoutPort = RequestMatcher(basicRate, "example.com", None, Some("some/path"))
    val lessSpecific = RequestMatcher(basicRate, "example.com", Some(9001))
    val leastSpecific = RequestMatcher(basicRate, "example.com")

    val matchers = Set(leastSpecific, lessSpecific, nextSpecific, mostSpecific, specificWithoutPort, shorterPathSpecificWithoutPort)

    def assertMatch(testUri: URI, expectedMatcher: RequestMatcher) =
      RequestMatcher.matchRequest(testUri, matchers) shouldBe Some(expectedMatcher)
  }

  "RequestMatcher" should "match the most specific matcher for a URI with matching host, port, and all path segments" in new TestScope {
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
    RequestMatcher.matchRequest(new URI("http://some-other-example.com/some/path"), matchers) shouldBe None
  }

}
