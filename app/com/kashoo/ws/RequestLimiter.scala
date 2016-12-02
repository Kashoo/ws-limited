package com.kashoo.ws

case class RequestLimiter(val requestMatcher: RequestMatcher, val rateLimit: RateLimit) {
}
