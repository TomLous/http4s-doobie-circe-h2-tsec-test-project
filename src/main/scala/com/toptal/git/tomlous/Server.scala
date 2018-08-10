package com.toptal.git.tomlous

import config.{AppConfig, DatabaseConfig}
import cats.effect.{Effect, IO}
import com.toptal.git.tomlous.util.WeatherBitApi
import fs2.{Stream, StreamApp}
import org.http4s.blaze.http._
import org.http4s.server.blaze.BlazeBuilder
import org.http4s.client.blaze._
// import org.http4s.client.blaze._

import org.http4s.client._

import cats.effect.IO
import com.sun.tools.internal.xjc.reader.xmlschema.bindinfo.BIConversion.User
import com.toptal.git.tomlous.dao.{JoggingTimeDAO, UserDAO}
import fs2.{Stream, StreamApp}
import fs2.StreamApp.ExitCode
import org.http4s.dsl.Http4sDsl
import org.http4s.server.blaze.BlazeBuilder
import service.{JoggingTimeService, UserService}

import scala.concurrent.ExecutionContext.Implicits.global
import tsec.passwordhashers.jca.BCrypt

import scala.concurrent.ExecutionContext.Implicits.global


object Server extends StreamApp[IO] with Http4sDsl[IO] {
  def stream(args: List[String], requestShutdown: IO[Unit]): Stream[IO, ExitCode] = {
    for {
      config <- Stream.eval(AppConfig.load())
      transactor <- Stream.eval(DatabaseConfig.transactor(config.database))
      _ <- Stream.eval(DatabaseConfig.initialize(transactor))
      httpClient <- Http1Client.stream[IO]()
      exitCode <- BlazeBuilder[IO]
        .bindHttp(config.server.port, config.server.host)
        .mountService(JoggingTimeService(JoggingTimeDAO(transactor),WeatherBitApi(httpClient, config.weatherBitApi)).service, "/")
        .mountService(UserService(UserDAO(transactor)).service, "/")
        .serve
    } yield exitCode
  }
}
