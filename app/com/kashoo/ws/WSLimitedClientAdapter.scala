package com.kashoo.ws

import java.net.URI

import com.kashoo.ws.RequestRateLimits.RequestRateLimits
import play.api.libs.ws.{WSClient, WSRequest}

/**
  * Wraps a WSClient, applying a limiter to URLs matched by configured rate limits.
  *
  * @param client underlying client implementation
  * @param requestRateLimits configured rate limits
  */
case class WSLimitedClientAdapter (client: WSClient, requestRateLimits: RequestRateLimits) extends WSClient {

  override def url(url: String): WSRequest = {
    RequestRateLimits.matchRequest(new URI(url), requestRateLimits) match {
      case Some(requestRateLimit) => WSLimitedRequestAdapter(client.url(url), requestRateLimit.rateLimit)
      case None => client.url(url)
    }
  }

  override def underlying[T]: T = client.underlying

  override def close(): Unit = client.close()
}
