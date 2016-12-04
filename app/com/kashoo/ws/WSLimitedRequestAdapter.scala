package com.kashoo.ws

import play.api.libs.iteratee.Enumerator
import play.api.libs.ws._

import scala.concurrent.Future

/**
  * Wraps a WSRequest, applying a request rate limit to the outgoing execution
  * @param wsRequest underlying request
  * @param rateLimit rate limit to apply to outgoing request
  */
case class WSLimitedRequestAdapter(wsRequest: WSRequest, rateLimit: RateLimit) extends WSRequest {
  override val url: String = wsRequest.url

  override def withHeaders(hdrs: (String, String)*): WSRequestHolder =  WSLimitedRequestAdapter(wsRequest.withHeaders(hdrs:_*), rateLimit)

  override def withAuth(username: String, password: String, scheme: WSAuthScheme): WSRequestHolder =  WSLimitedRequestAdapter(wsRequest.withAuth(username, password, scheme), rateLimit)

  override def withQueryString(parameters: (String, String)*): WSRequestHolder =  WSLimitedRequestAdapter(wsRequest.withQueryString(parameters:_*), rateLimit)

  override def execute(): Future[WSResponse] = rateLimit.limit(wsRequest.execute)

  override def sign(calc: WSSignatureCalculator): WSRequestHolder =  WSLimitedRequestAdapter(wsRequest.sign(calc), rateLimit)

  override def stream(): Future[(WSResponseHeaders, Enumerator[Array[Byte]])] = rateLimit.limit(wsRequest.stream)

  override def withVirtualHost(vh: String): WSRequestHolder =  WSLimitedRequestAdapter(wsRequest.withVirtualHost(vh), rateLimit)

  override def withMethod(method: String): WSRequestHolder = WSLimitedRequestAdapter(wsRequest.withMethod(method), rateLimit)

  override def withRequestTimeout(timeout: Long): WSRequestHolder =  WSLimitedRequestAdapter(wsRequest.withRequestTimeout(timeout), rateLimit)

  override def withProxyServer(proxyServer: WSProxyServer): WSRequestHolder =  WSLimitedRequestAdapter(wsRequest.withProxyServer(proxyServer), rateLimit)

  override def withFollowRedirects(follow: Boolean): WSRequestHolder =  WSLimitedRequestAdapter(wsRequest.withFollowRedirects(follow), rateLimit)

  override def withBody(body: WSBody): WSRequestHolder =  WSLimitedRequestAdapter(wsRequest.withBody(body), rateLimit)

  override val calc: Option[WSSignatureCalculator] = wsRequest.calc
  override val queryString: Map[String, Seq[String]] = wsRequest.queryString
  override val method: String = wsRequest.method
  override val followRedirects: Option[Boolean] = wsRequest.followRedirects
  override val body: WSBody = wsRequest.body
  override val requestTimeout: Option[Int] = wsRequest.requestTimeout
  override val virtualHost: Option[String] = wsRequest.virtualHost
  override val proxyServer: Option[WSProxyServer] = wsRequest.proxyServer
  override val auth: Option[(String, String, WSAuthScheme)] = wsRequest.auth
  override val headers: Map[String, Seq[String]] = wsRequest.headers
}
