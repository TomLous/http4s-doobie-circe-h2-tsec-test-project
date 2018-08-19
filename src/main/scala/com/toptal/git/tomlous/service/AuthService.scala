package com.toptal.git.tomlous.service

import com.toptal.git.tomlous.dao._
import com.toptal.git.tomlous.model._

import cats.implicits._
import org.http4s.{AuthedService, HttpService}
import org.http4s.server.syntax._
import io.circe._
import io.circe.java8.time._
import cats.effect.IO
import com.toptal.git.tomlous.auth.BearerTokenBackingStore
import com.toptal.git.tomlous.config.AuthConfig
import com.typesafe.scalalogging.LazyLogging
import org.http4s.dsl.Http4sDsl
import org.http4s.circe._
import io.circe.generic.auto._
import io.circe.syntax._
import org.http4s.server.AuthMiddleware
import org.http4s.server.middleware.authentication.BasicAuth
import tsec.authentication.TSecBearerToken
import tsec.common.SecureRandomId


case class AuthService(dao: AuthDAO, tokenStore: BearerTokenBackingStore, authConfig: AuthConfig) extends Http4sDsl[IO] with LazyLogging {

  private val basicAuth: AuthMiddleware[IO, User] = BasicAuth(authConfig.basicRealm, dao.login)

  private val authEndpoint = AuthedService[User, IO] {
    case GET -> Root / "token" as user => {
      val bearerToken = TSecBearerToken(SecureRandomId.Strong.generate, user.id.get, authConfig.tokenExpiry, None)
      tokenStore.put(bearerToken)

      logger.info(s"Adding bearer token ${bearerToken.id.toString} for ${user.username} valid until ${bearerToken.expiry}" )
      Ok(TokenResponse(bearerToken.id.toString, bearerToken.expiry).asJson)
    }
  }

  val service: HttpService[IO] = basicAuth(authEndpoint)

}
