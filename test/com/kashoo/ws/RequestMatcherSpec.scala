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
    val lessSpecific = RequestMatcher(basicRate, "example.com", Some(9001))
    val leastSpecific = RequestMatcher(basicRate, "example.com")

    val matchers = Set(leastSpecific, lessSpecific, nextSpecific, mostSpecific)

    def assertMatch(testUri: URI, expectedMatcher: RequestMatcher) =
      RequestMatcher.matchRequest(testUri, matchers) shouldBe Some(expectedMatcher)
  }

  "RequestMatcher" should "match the most specific matcher for a URI matching all path segments" in new TestScope {
    assertMatch(new URI("http://example.com:9001/some/path/with/several/segments/endpoint"), mostSpecific)
  }

  it should "match the next most specific matcher for a URI matching all path segments of a less specific matcher" in new TestScope {
    assertMatch(new URI("http://example.com:9001/some/path/with/different/segments"), nextSpecific)
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
