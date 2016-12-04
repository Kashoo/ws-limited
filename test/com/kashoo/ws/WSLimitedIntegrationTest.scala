package com.kashoo.ws

import java.util.concurrent.TimeUnit

import org.mockito.Matchers._
import org.mockito.Mockito._
import org.mockito.invocation.InvocationOnMock
import org.mockito.stubbing.Answer
import org.scalatest.mock.MockitoSugar
import org.scalatest.{FlatSpec, Matchers}
import play.Logger
import play.api.libs.ws.{WSClient, WSRequest, WSResponse}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._
import scala.concurrent.{Await, Future}

class WSLimitedIntegrationTest extends FlatSpec with MockitoSugar with Matchers {

  /** Rudimentary timer */
  def time[R](block: => R): Duration = {
    val t0 = System.nanoTime()
    val result = block
    val t1 = System.nanoTime()
    Duration(t1 - t0, TimeUnit.NANOSECONDS)
  }

  "WSLimitedClientWrapper" should "properly limit outgoing API requests" in {
    // given
    val rateLimitConfig = RequestRateLimits(RequestRateLimit(Rate(5, 30.seconds), RequestMatcher("localhost", Some(9001), Some("specific/path"))),
                                            RequestRateLimit(Rate(1, 30.seconds), RequestMatcher("localhost", Some(9001))))

    val mockClient = mock[WSClient]
    when(mockClient.url(any[String])).thenAnswer(new Answer[WSRequest] {
      override def answer(invocation: InvocationOnMock): WSRequest = {
        val mockReq = mock[WSRequest]
        when(mockReq.url).thenReturn(invocation.getArguments.head.asInstanceOf[String])
        when(mockReq.withMethod(any[String])).thenReturn(mockReq)
        when(mockReq.get()).thenReturn(Future.successful(mock[WSResponse]))
        when(mockReq.execute()).thenReturn(Future.successful(mock[WSResponse]))
        mockReq
      }
    })

    val client: WSClient = WSLimitedClientAdapter(mockClient, rateLimitConfig)

    // will all fire almost instantaneously
    assert(time {
      Await.result(Future.sequence(Range(0,1000).map { i => client.url(s"http://localhost:9002/$i").get() }), 2.seconds)
    }.lteq(2.seconds))


    // at only 1 per 30 seconds, issuing 4 requests should last at ~90s
    val s1f1Time = time {
      Await.result(Future.sequence(Range(0, 4).map { i => client.url(s"http://localhost:9001/$i").get() }), 2.minutes)
    }
    val ninetySeconds = 90.seconds
    Logger.info(s"1st endpoint duration: ${s1f1Time.toSeconds}s")
    assert(s1f1Time.lteq(ninetySeconds + 5.second))


    // at 5 per 30s, issuing 10 requests should last < 1 minute
    val s1f2Time = time {
      Await.result(Future.sequence(Range(0, 10).map { i => client.url(s"http://localhost:9001/specific/path/$i").get() }), 1.minute)
    }
    Logger.info(s"2nd endpoint duration: ${s1f2Time.toSeconds}s")
    assert(s1f2Time.lteq(1.minute))
  }


}
