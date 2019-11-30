package com.kashoo.ws

import akka.actor.ActorSystem
import javax.inject.{Inject, Provider}
import play.api.Configuration
import play.api.libs.ws.WSClient

import scala.concurrent.ExecutionContext

/**
  * Provider that wraps clients provided by WSAPI with [[com.kashoo.ws.WSLimitedClientAdapter]]
  *
  * @param config application configuration
  * @param ws WSClient implementation
  */
class WSLimitedClientProvider @Inject() (config: Configuration, ws: WSClient, actorSystem: ActorSystem)(implicit ec: ExecutionContext) extends Provider[WSClient] {

  val limitedClient = WSLimitedClientAdapter(ws, RequestRateLimits(config, actorSystem))

  override def get(): WSClient = limitedClient
}
