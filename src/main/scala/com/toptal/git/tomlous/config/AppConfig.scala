package com.toptal.git.tomlous.config

import cats.effect.IO
import com.typesafe.config.ConfigFactory
import cats.implicits._
import pureconfig.error.ConfigReaderException

case class AppConfig(server: ServerConfig, database: DatabaseConfig, weatherBitApi: WeatherBitConfig)

object AppConfig {

  import pureconfig._

  def load(configFile: String = "application.conf"): IO[AppConfig] =
    IO {
      loadConfig[AppConfig](ConfigFactory.load(configFile), "toptal")
    }.flatMap {
      case Left(e) => IO.raiseError[AppConfig](new ConfigReaderException[AppConfig](e))
      case Right(config) => IO.pure(config)
    }

}