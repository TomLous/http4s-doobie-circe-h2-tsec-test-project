package com.toptal.git.tomlous.util


import java.time.temporal.ChronoUnit
import com.toptal.git.tomlous.config.WeatherBitConfig
import org.http4s.client._
import org.f100ded.scalaurlbuilder.URLBuilder
import cats.effect.IO
import com.toptal.git.tomlous.model.JoggingTime
import io.circe.Json
import org.http4s._
import org.http4s.dsl.io._
import org.http4s.client.dsl.io._
import org.http4s.circe._
import cats.syntax.either._
import com.typesafe.scalalogging.LazyLogging
import io.circe.optics.JsonPath._
import java.time.format.DateTimeFormatter


case class WeatherBitApi(httpClient: Client[IO], weatherBitApiConfig: WeatherBitConfig) extends LazyLogging {

  val baseUrl = "https://api.weatherbit.io/v2.0/history/hourly"

  val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd:HH")

  def updateWeatherConditions(joggingTime: JoggingTime) = {

    // Add duration to start time to get end time
    val officialEndDate = joggingTime.datetime
      .plusHours(joggingTime.duration.getHour)
      .plusMinutes(joggingTime.duration.getMinute)
      .plusSeconds(joggingTime.duration.getSecond)

    // End time needs to be at least +1 hour from start date
    val endDate =
      if (ChronoUnit.HOURS.between(joggingTime.datetime, officialEndDate) > 0) officialEndDate
      else joggingTime.datetime.plusHours(1)

    // build url according to api spec (https://www.weatherbit.io/api/weather-history-hourly)
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

    // build lens
    val weatherDescriptionPath = root.data.each.weather.description.string

    // run client call
    val weatherInfo = for {
      uri <- Uri.fromString(urlString)
      request = GET(uri)
      response = httpClient.expect[Json](request)
      json <- Either.catchNonFatal(response.unsafeRunSync)
      weatherDescriptions <- Either.catchNonFatal(weatherDescriptionPath.getAll(json).distinct.mkString(", "))
    } yield weatherDescriptions

    // Always return the joggingTime, optionally with weather data
    weatherInfo match {
      case Left(e) =>
        logger.warn(s"WeatherBit retrieval failed from `$urlString`: ${e.getMessage}")
        joggingTime
      case Right(weather) =>
        logger.debug(s"WeatherBit retrieved '$weather'")
        joggingTime.copy(weather = Option(weather))
    }
  }

}
