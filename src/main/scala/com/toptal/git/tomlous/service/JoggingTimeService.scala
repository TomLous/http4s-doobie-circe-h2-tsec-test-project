package com.toptal.git.tomlous.service


import cats.data.EitherT
import cats.effect.IO
import cats.implicits._
import cats.SemigroupK._
import com.toptal.git.tomlous.auth.AuthedServiceHandler
import com.toptal.git.tomlous.dao.JoggingTimeDAO
import com.toptal.git.tomlous.model.{JoggingTime, User}
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
import tsec.authentication._


case class JoggingTimeService(dao: JoggingTimeDAO, weatherBitApi: WeatherBitApi, authedServiceHandler:AuthedServiceHandler) extends CrudService[JoggingTime](dao, authedServiceHandler) {

//  val weatherService = TSecAuthService.withAuthorization(AuthService.) {
  val weatherService =  TSecAuthService.withAuthorization(authedServiceHandler.everyOne){

    case req@POST -> Root asAuthed user =>
      val complex = for {
        item <- EitherT.right(req.request.decodeJson[JoggingTime])
        updatedItem = weatherBitApi.updateWeatherConditions(item)
        createdResult <- EitherT(dao.create(updatedItem))
        getResult <- EitherT(dao.get(createdResult.id.get))
      } yield getResult

      for {
        getResult <- complex.value
        response <- result(getResult)
      } yield response

    case req@PUT -> Root / LongVar(id) asAuthed user =>
      for {
        item <- req.request.decodeJson[JoggingTime]
        updatedItem = weatherBitApi.updateWeatherConditions(item)
        updateResult <- dao.update(id, updatedItem)
        response <- result(updateResult)
      } yield response
  }

//  weatherService
  // TSecAuthService.withAuthorization(AuthService.)

  val service =  authedServiceHandler.securedRequestHandler.liftService(weatherService <+> crudService)
}
