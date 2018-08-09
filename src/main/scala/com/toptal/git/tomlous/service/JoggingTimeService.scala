package com.toptal.git.tomlous.service


import cats.effect.IO
import cats.implicits._

import com.toptal.git.tomlous.dao.JoggingTimeDAO
import com.toptal.git.tomlous.dao.error.NotFoundError
import com.toptal.git.tomlous.model.JoggingTime



import org.http4s.HttpService
import org.http4s.dsl.Http4sDsl
import org.http4s.circe._

import io.circe._
import io.circe.syntax._
import io.circe.generic.auto._
import io.circe.java8.time._




case class JoggingTimeService (dao: JoggingTimeDAO) extends Http4sDsl[IO] {
//  private implicit val encodeUserRole: Encoder[UserRole] = Encoder.encodeString.contramap[UserRole](_.value)

//  private implicit val decodeUserRole: Decoder[UserRole] = Decoder.decodeString.map[UserRole](UserRole.unsafeFromString)

  val Endpoint =  "joggingtime"


  val service = HttpService[IO] {
    case GET -> Root / Endpoint =>
      for {
        getResult <- dao.list
        response <- Ok(getResult.asJson)
      } yield response


    case GET -> Root / Endpoint / LongVar(id) =>
      for {
        getResult <- dao.get(id)
        response <- result(getResult)
      } yield response

    case req @ POST -> Root / Endpoint =>
      for {
        item <- req.decodeJson[JoggingTime]
        createdItem <- dao.create(item)
        getResult <- dao.get(createdItem.id.get)
        response <- result(getResult)
      } yield response

    case req @ PUT -> Root / Endpoint / LongVar(id) =>
      for {
        item <-req.decodeJson[JoggingTime]
        updateResult <- dao.update(id, item)
        response <- result(updateResult)
      } yield response

    case DELETE -> Root / Endpoint / LongVar(id) =>
      dao.delete(id).flatMap {
        case Left(NotFoundError) => NotFound()
        case Right(_) => NoContent()
      }
  }

  private def result(result: Either[NotFoundError.type, JoggingTime]) = {
    result match {
      case Left(NotFoundError) => NotFound()
      case Right(joggingTime) => Ok(joggingTime.asJson)
    }
  }
}
