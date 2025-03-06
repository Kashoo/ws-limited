package com.kashoo.ws

import java.util.concurrent.TimeUnit

import org.mockito.Mockito._
import org.mockito.invocation.InvocationOnMock
import org.mockito.stubbing.Answer
import org.specs2.mock.Mockito
import org.specs2.specification.Scope
import play.api.Logger
import play.api.libs.ws.{WSClient, WSRequest, WSResponse}
import play.api.test.PlaySpecification

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._
import scala.concurrent.{Await, Future}

class WSLimitedIntegrationTest extends PlaySpecification with Mockito {

  val logger: Logger = Logger(this.getClass)
  /** Rudimentary timer */
  def time[R](block: => R): Duration = {
    val t0: Long = System.nanoTime()
    val result: R = block
    val t1: Long = System.nanoTime()
    Duration(t1 - t0, TimeUnit.NANOSECONDS)
  }

  class TestScope extends Scope {
    val rateLimitConfig: Set[RequestRateLimit] = RequestRateLimits(
      RequestRateLimit(Rate(5, 10.seconds), RequestMatcher("localhost", Some(9001), Some("specific/path"))),
      RequestRateLimit(Rate(1, 10.seconds), RequestMatcher("localhost", Some(9001)))
    )

    val mockClient: WSClient = mock[WSClient]
    when(mockClient.url(anyString)).thenAnswer(new Answer[WSRequest] {
      override def answer(invocation: InvocationOnMock): WSRequest = {
        val mockReq: WSRequest = mock[WSRequest]
        when(mockReq.url).thenReturn(invocation.getArguments.head.asInstanceOf[String])
        when(mockReq.withMethod(anyString)).thenReturn(mockReq)
        when(mockReq.get()).thenReturn(Future.successful(mock[WSResponse]))
        when(mockReq.execute()).thenReturn(Future.successful(mock[WSResponse]))
        mockReq
      }
    })

    val client: WSClient = WSLimitedClientAdapter(mockClient, rateLimitConfig)
  }

  "WSLimitedClientWrapper" should {
    "properly limit outgoing API requests" in new TestScope {
      // will all fire almost instantaneously
      val unrestricted: Duration = time {
        Await.result(Future.sequence(Range(0,1000).map { i =>
          client.url(s"http://localhost:9002/$i").get()
        }), 2.seconds)
      }
      logger.info(s"Unrestricted duration: ${unrestricted.toMillis}ms")
      unrestricted must be_<=(2.seconds)

      // at only 1 per 10 seconds, issuing 4 requests should last at ~30s
      val s1f1Time: Duration = time {
        Await.result(Future.sequence(Range(0, 4).map { i =>
          client.url(s"http://localhost:9001/$i").get()
        }), 60.seconds)
      }
      logger.info(s"1st endpoint duration: ${s1f1Time.toSeconds}s")
      s1f1Time must be_<=(50.seconds)

      // at 5 per 10s, issuing 10 requests should last < 20 seconds
      val s1f2Time: Duration = time {
        Await.result(Future.sequence(Range(0, 10).map { i =>
          client.url(s"http://localhost:9001/specific/path/$i").get()
        }), 60.seconds)
      }
      logger.info(s"2nd endpoint duration: ${s1f2Time.toSeconds}s")
      s1f2Time must be_<=(25.seconds)
    }
  }
}
