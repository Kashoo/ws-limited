package com.kashoo.ws

import javax.inject.Singleton

import play.api.inject.Module
import play.api.libs.ws.WSClient
import play.api.{Configuration, Environment}

class WSLimitedModule extends Module {
  def bindings(environment: Environment, configuration: Configuration) = {
    Seq(
      bind[WSClient].toProvider[WSLimitedClientProvider].in[Singleton]
    )
  }
}