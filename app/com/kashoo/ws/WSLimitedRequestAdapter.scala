package com.kashoo.ws

import java.io.File
import java.net.URI

import akka.stream.scaladsl.Source
import akka.util.ByteString
import play.api.libs.ws._
import play.api.mvc.MultipartFormData

import scala.concurrent.Future
import scala.concurrent.duration.Duration

/**
  * Wraps a WSRequest, applying a request rate limit to the outgoing execution
  *
  * @param wsRequest underlying request
  * @param rateLimit rate limit to apply to outgoing request
  */
case class WSLimitedRequestAdapter(wsRequest: WSRequest, rateLimit: RateLimit) extends WSRequest {
  override val url: String = wsRequest.url

  override def withHttpHeaders(hdrs: (String, String)*): WSRequest =  WSLimitedRequestAdapter(wsRequest.withHttpHeaders(hdrs:_*), rateLimit)

  @deprecated
  override def withHeaders(hdrs: (String, String)*): WSRequest = WSLimitedRequestAdapter(wsRequest.withHttpHeaders(hdrs:_*), rateLimit)

  override def withAuth(username: String, password: String, scheme: WSAuthScheme): WSRequest =  WSLimitedRequestAdapter(wsRequest.withAuth(username, password, scheme), rateLimit)

  override def withQueryStringParameters(parameters: (String, String)*): WSRequest = WSLimitedRequestAdapter(wsRequest.withQueryStringParameters(parameters:_*), rateLimit)

  @deprecated
  override def withQueryString(parameters: (String, String)*): WSRequest = WSLimitedRequestAdapter(wsRequest.withQueryStringParameters(parameters:_*), rateLimit)

  override def execute(): Future[WSResponse] = rateLimit.limit(wsRequest.execute())

  override def sign(calc: WSSignatureCalculator): WSRequest =  WSLimitedRequestAdapter(wsRequest.sign(calc), rateLimit)

  override def withVirtualHost(vh: String): WSRequest =  WSLimitedRequestAdapter(wsRequest.withVirtualHost(vh), rateLimit)

  override def withMethod(method: String): WSRequest = WSLimitedRequestAdapter(wsRequest.withMethod(method), rateLimit)

  override def withRequestTimeout(timeout: Duration): WSRequest =  WSLimitedRequestAdapter(wsRequest.withRequestTimeout(timeout), rateLimit)

  override def withProxyServer(proxyServer: WSProxyServer): WSRequest =  WSLimitedRequestAdapter(wsRequest.withProxyServer(proxyServer), rateLimit)

  override def withFollowRedirects(follow: Boolean): WSRequest =  WSLimitedRequestAdapter(wsRequest.withFollowRedirects(follow), rateLimit)

  override val calc: Option[WSSignatureCalculator] = wsRequest.calc
  override val queryString: Map[String, Seq[String]] = wsRequest.queryString
  override val method: String = wsRequest.method
  override val followRedirects: Option[Boolean] = wsRequest.followRedirects
  override val body: WSBody = wsRequest.body
  override val requestTimeout: Option[Duration] = wsRequest.requestTimeout
  override val virtualHost: Option[String] = wsRequest.virtualHost
  override val proxyServer: Option[WSProxyServer] = wsRequest.proxyServer
  override val auth: Option[(String, String, WSAuthScheme)] = wsRequest.auth
  override val headers: Map[String, Seq[String]] = wsRequest.headers

  override def withRequestFilter(filter: WSRequestFilter): WSRequest = WSLimitedRequestAdapter(wsRequest.withRequestFilter(filter), rateLimit)

  override def withCookies(cookie: WSCookie*): WSRequest = WSLimitedRequestAdapter(wsRequest.withCookies(cookie:_*), rateLimit)

  override def withBody[T](body: T)(implicit evidence$1: BodyWritable[T]): WSRequest = WSLimitedRequestAdapter(wsRequest.withBody(body), rateLimit)

  override def get(): Future[WSResponse] = rateLimit.limit(wsRequest.get())

  override def post[T](body: T)(implicit evidence$2: BodyWritable[T]): Future[WSResponse] = rateLimit.limit(wsRequest.post(body))

  override def post(body: File): Future[WSResponse] = rateLimit.limit(wsRequest.post(body))

  override def post(body: Source[MultipartFormData.Part[Source[ByteString, _]], _]): Future[WSResponse] = rateLimit.limit(wsRequest.post(body))

  override def patch[T](body: T)(implicit evidence$3: BodyWritable[T]): Future[WSResponse] = rateLimit.limit(wsRequest.patch(body))

  override def patch(body: File): Future[WSResponse] = rateLimit.limit(wsRequest.patch(body))

  override def patch(body: Source[MultipartFormData.Part[Source[ByteString, _]], _]): Future[WSResponse] = rateLimit.limit(wsRequest.patch(body))

  override def put[T](body: T)(implicit evidence$4: BodyWritable[T]): Future[WSResponse] = rateLimit.limit(wsRequest.put(body))

  override def put(body: File): Future[WSResponse] = rateLimit.limit(wsRequest.put(body))

  override def put(body: Source[MultipartFormData.Part[Source[ByteString, _]], _]): Future[WSResponse] = rateLimit.limit(wsRequest.put(body))

  override def delete(): Future[WSResponse] = rateLimit.limit(wsRequest.delete())

  override def head(): Future[WSResponse] = rateLimit.limit(wsRequest.head())

  override def options(): Future[WSResponse] = rateLimit.limit(wsRequest.options())

  override def execute(method: String): Future[WSResponse] = rateLimit.limit(wsRequest.execute(method))

  override def uri: URI = wsRequest.uri

  override def contentType: Option[String] = wsRequest.contentType

  override def cookies: Seq[WSCookie] = wsRequest.cookies

  override def stream(): Future[WSResponse] = rateLimit.limit(wsRequest.stream())

  override def withUrl(url: String): WSRequest = WSLimitedRequestAdapter(wsRequest.withUrl(url), rateLimit)
}
