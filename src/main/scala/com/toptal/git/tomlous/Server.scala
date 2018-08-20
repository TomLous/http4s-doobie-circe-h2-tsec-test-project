package com.toptal.git.tomlous

import config.{AppConfig, DatabaseConfig}
import cats.effect.{Effect, IO}
import com.toptal.git.tomlous.util.WeatherBitApi
import fs2.{Stream, StreamApp}
import org.http4s.blaze.http._
import org.http4s.server.blaze.BlazeBuilder
import org.http4s.client.blaze._
import org.http4s.client._
import cats.effect.IO
import com.toptal.git.tomlous.auth.{AuthedServiceHandler, InMemoryBearerTokenBackingStore, UserBackingStore}
import com.toptal.git.tomlous.dao._
import com.toptal.git.tomlous.model.User
import fs2.{Stream, StreamApp}
import fs2.StreamApp.ExitCode
import org.http4s.dsl.Http4sDsl
import org.http4s.server.blaze.BlazeBuilder
import service._
import tsec.authentication.{BackingStore, BearerTokenAuthenticator, SecuredRequestHandler, TSecBearerToken}

import scala.concurrent.ExecutionContext.Implicits.global


object Server extends StreamApp[IO] with Http4sDsl[IO] {


  def stream(args: List[String], requestShutdown: IO[Unit]): Stream[IO, ExitCode] = {
    for {
      config <- Stream.eval(AppConfig.load())
      transactor <- Stream.eval(DatabaseConfig.transactor(config.database))
      _ <- Stream.eval(DatabaseConfig.initialize(transactor))
      httpClient <- Http1Client.stream[IO]()
      authedServiceHandler <- Stream(AuthedServiceHandler(UserDAO(transactor), InMemoryBearerTokenBackingStore, config.auth))
      exitCode <- BlazeBuilder[IO]
        .bindHttp(config.server.port, config.server.host)
        .mountService(
          AuthService(AuthDAO(transactor), InMemoryBearerTokenBackingStore, config.auth).service, "/auth")
        .mountService(
          JoggingTimeService(JoggingTimeDAO(transactor), WeatherBitApi(httpClient, config.weatherBitApi), authedServiceHandler).service, "/joggingtime")
        .mountService(
          UserService(UserDAO(transactor), authedServiceHandler).service, "/user")
        .serve
    } yield exitCode
  }
}
