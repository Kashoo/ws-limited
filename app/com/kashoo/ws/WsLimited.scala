package com.kashoo.ws

import play.api.libs.ws.{WSRequest, WSResponse}

import scala.concurrent.Future

/**
  * What do I need?
  * - access to request matchers / rates pairs
  * -
  * - way to configure?
  */
trait WsLimitedRequest extends WSRequest {

//  override def execute(): Future[WSResponse] = {
//    // need to match this bitch against request matchers with installed rates
//
//    super.execute()
//  }



}
