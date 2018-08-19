package com.toptal.git.tomlous.service


import cats.data.EitherT
import cats.effect.IO
import cats.implicits._
import cats.SemigroupK._
import com.toptal.git.tomlous.dao.JoggingTimeDAO
import com.toptal.git.tomlous.model.JoggingTime
import com.toptal.git.tomlous.service.meta.CrudService
import com.toptal.git.tomlous.util.WeatherBitApi
import org.http4s.HttpService
import org.http4s.dsl.Http4sDsl
import org.http4s.circe._
import org.http4s.server.syntax._
import io.circe._
import io.circe.syntax._
import io.circe.generic.auto._
import io.circe.java8.time._


case class JoggingTimeService(dao: JoggingTimeDAO, weatherBitApi: WeatherBitApi) extends CrudService[JoggingTime](dao, "joggingtime") {

  val weatherService = HttpService[IO] {

    case req@POST -> Root / Endpoint =>
      val complex = for {
        item <- EitherT.right(req.decodeJson[JoggingTime])
        updatedItem = weatherBitApi.updateWeatherConditions(item)
        createdResult <- EitherT(dao.create(updatedItem))
        getResult <- EitherT(dao.get(createdResult.id.get))
      } yield getResult

      for {
        getResult <- complex.value
        response <- result(getResult)
      } yield response

    case req@PUT -> Root / Endpoint / LongVar(id) =>
      for {
        item <- req.decodeJson[JoggingTime]
        updatedItem = weatherBitApi.updateWeatherConditions(item)
        updateResult <- dao.update(id, updatedItem)
        response <- result(updateResult)
      } yield response
  }

  val service = weatherService <+> crudService
}
