package com.kashoo.ws

import javax.inject.{Inject, Provider}
import play.api.Configuration
import play.api.libs.ws.WSClient

/**
  * Provider that wraps clients provided by WSAPI with [[com.kashoo.ws.WSLimitedClientAdapter]]
  *
  * @param config application configuration
  * @param ws WSClient implementation
  */
class WSLimitedClientProvider @Inject() (config: Configuration, ws: WSClient) extends Provider[WSClient] {

  val limitedClient = WSLimitedClientAdapter(ws, RequestRateLimits(config))

  override def get(): WSClient = limitedClient
}
