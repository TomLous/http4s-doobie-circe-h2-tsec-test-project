package com.toptal.git.tomlous.util

import java.net.URL
import java.time.temporal.ChronoUnit

import cats.data.EitherT
import cats.effect.IO
import com.toptal.git.tomlous.config.WeatherBitConfig
import org.http4s.client._
import org.http4s.client.blaze._
import org.f100ded.scalaurlbuilder.URLBuilder
import cats.effect.IO
import com.toptal.git.tomlous.model.JoggingTime
import io.circe.Json
import io.circe.generic.auto._
import org.http4s._
import org.http4s.dsl.io._
import org.http4s.client.dsl.io._
import org.http4s.headers._
import org.http4s.MediaType
import org.http4s.circe._
import org.http4s.client.blaze._
import cats.syntax.either._
import com.typesafe.scalalogging.LazyLogging
import io.circe.optics.JsonPath._



case class WeatherBitApi(httpClient: Client[IO], weatherBitApiConfig: WeatherBitConfig) extends LazyLogging{

  import org.http4s.client.dsl.io._

  val baseUrl = "https://api.weatherbit.io/v2.0/history/hourly"

  import java.time.format.DateTimeFormatter

  val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd:HH")

  def weatherConditions(joggingTime: JoggingTime) = {

    val officialEndDate = joggingTime.datetime
      .plusHours(joggingTime.duration.getHour)
      .plusMinutes(joggingTime.duration.getMinute)
      .plusSeconds(joggingTime.duration.getSecond)

    val endDate =
      if (ChronoUnit.HOURS.between(joggingTime.datetime, officialEndDate) > 0) officialEndDate
      else joggingTime.datetime.plusHours(1)

    val urlString = URLBuilder(baseUrl)
      .withQueryParameters(
        "key" -> weatherBitApiConfig.key,
        "city" -> joggingTime.location,
        "start_date" -> joggingTime.datetime.format(formatter),
        "end_date" -> endDate.format(formatter),
        "lang" -> weatherBitApiConfig.language,
        "units" -> weatherBitApiConfig.units
      ).toString()

    logger.debug(s"WeatherBit url: $urlString")

    val weatherPath = root.data.each.weather.description.string

    val weather = for {
      uri <- Uri.fromString(urlString)
      request = GET(uri)
      response = httpClient.expect[Json](request)
      json <- Either.catchNonFatal(response.unsafeRunSync)
      weatherDescriptions <- Either.catchNonFatal(weatherPath.getAll(json).distinct.mkString(", "))
    } yield weatherDescriptions

    weather match {
      case Left(e) =>
        logger.warn(s"WeatherBit retrieval failed from `$urlString`: ${e.getMessage}")
        joggingTime
      case Right(weatherDescription) =>
        logger.debug(s"WeatherBit retrieved '$weatherDescription'")
        joggingTime.copy(weather = Option(weatherDescription))
    }
  }

}
