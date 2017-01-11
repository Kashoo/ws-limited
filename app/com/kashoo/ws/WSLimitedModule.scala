package com.kashoo.ws

import javax.inject.Singleton

import play.api.inject.{Binding, Module}
import play.api.libs.ws.WSClient
import play.api.{Configuration, Environment}

/**
  * Module for binding [[com.kashoo.ws.WSLimitedClientProvider]]
  */
class WSLimitedModule extends Module {
  override def bindings(environment: Environment, configuration: Configuration): Seq[Binding[_]] = {
    Seq(
      bind[WSClient].qualifiedWith(classOf[RateLimited]).toProvider[WSLimitedClientProvider].in[Singleton]
    )
  }
}