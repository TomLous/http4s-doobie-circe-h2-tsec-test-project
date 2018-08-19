package com.toptal.git.tomlous.config

import java.time.Instant
import scala.concurrent.duration._


import tsec.authentication.TSecTokenSettings

import scala.concurrent.duration.Duration

case class AuthConfig(basicRealm: String, tokenExpiration:Long) {
  lazy val tokenExpiry:Instant = Instant.now().plusSeconds(tokenExpiration)

  lazy val tokenSettings:TSecTokenSettings = TSecTokenSettings(
    expiryDuration = Duration(tokenExpiration, SECONDS),
    maxIdle = None
  )
}
