package com.kashoo.ws

import javax.inject.{Inject, Provider}

import play.api.Configuration
import play.api.libs.ws.{WSAPI, WSClient}

/**
  * Provider that wraps clients provided by [[play.api.libs.ws.WSAPI]] with [[com.kashoo.ws.WSLimitedClientAdapter]]
  *
  * @param config application configuration
  * @param wsApi WSAPI implementation
  */
class WSLimitedClientProvider @Inject() (config: Configuration, wsApi: WSAPI) extends Provider[WSClient] {

  val limitedClient = WSLimitedClientAdapter(wsApi.client, RequestRateLimits(config))

  override def get(): WSClient = limitedClient
}
