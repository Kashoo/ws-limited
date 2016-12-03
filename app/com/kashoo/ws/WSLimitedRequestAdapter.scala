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

  override def withHeaders(hdrs: (String, String)*): WSRequestHolder = wsRequest.withHeaders(hdrs:_*)

  override def withAuth(username: String, password: String, scheme: WSAuthScheme): WSRequestHolder = wsRequest.withAuth(username, password, scheme)

  override def withQueryString(parameters: (String, String)*): WSRequestHolder = wsRequest.withQueryString(parameters:_*)

  override def execute(): Future[WSResponse] = rateLimit.limit(wsRequest.execute)

  override def sign(calc: WSSignatureCalculator): WSRequestHolder = wsRequest.sign(calc)

  override def stream(): Future[(WSResponseHeaders, Enumerator[Array[Byte]])] = rateLimit.limit(wsRequest.stream)

  override def withVirtualHost(vh: String): WSRequestHolder = wsRequest.withVirtualHost(vh)

  override def withMethod(method: String): WSRequestHolder = wsRequest.withMethod(method)

  override def withRequestTimeout(timeout: Long): WSRequestHolder = wsRequest.withRequestTimeout(timeout)

  override def withProxyServer(proxyServer: WSProxyServer): WSRequestHolder = wsRequest.withProxyServer(proxyServer)

  override def withFollowRedirects(follow: Boolean): WSRequestHolder = wsRequest.withFollowRedirects(follow)

  override def withBody(body: WSBody): WSRequestHolder = wsRequest.withBody(body)

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
