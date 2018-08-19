package com.toptal.git.tomlous.config

import java.time.Instant

case class AuthConfig(basicRealm: String, tokenExpiration:Long) {
  lazy val tokenExpiry = Instant.now().plusSeconds(tokenExpiration)
}
