package com.kashoo.ws

import javax.inject.{Inject, Provider}

import play.api.Configuration
import play.api.libs.ws.{WSAPI, WSClient}

class WSLimitedClientProvider @Inject() (config: Configuration, wsApi: WSAPI) extends Provider[WSClient] {

  val limitedClient = WSLimitedClientAdapter(wsApi.client, RequestRateLimits(config))

  override def get(): WSClient = limitedClient
}
