package com.kashoo.ws

import java.net.URI

import com.google.inject.{Inject, Singleton}
import com.kashoo.ws.RequestRateLimits.RequestRateLimits
import play.api.libs.ws.{WSAPI, WSClient, WSRequest}

import scala.concurrent.ExecutionContext

@Singleton
class WsLimitedAdapter @Inject() (wsAPI: WSAPI, requestRateLimits: RequestRateLimits)(implicit ec: ExecutionContext) extends WSAPI {

  override def client: WSClient = wsAPI.client

  override def url(url: String): WSRequest = {
    RequestRateLimits.matchRequest(new URI(url), requestRateLimits) match {
      case Some(requestRateLimit) => WSLimitedRequestAdapter(client.url(url), requestRateLimit.rateLimit)
      case None => client.url(url)
    }
  }
}
