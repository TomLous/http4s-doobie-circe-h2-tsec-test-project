package com.toptal.git.tomlous.service.meta

import cats.effect.IO
import com.toptal.git.tomlous.dao.error.NotFoundError
import com.toptal.git.tomlous.dao.meta.CrudDAO
import com.toptal.git.tomlous.model.meta.DBItem
import io.circe._
import io.circe.syntax._
import org.http4s.HttpService
import org.http4s.circe._
import org.http4s.dsl.Http4sDsl

abstract class  CrudService[T <: DBItem](dao: CrudDAO[T], Endpoint: String)(implicit val encItem: Encoder[T], encList: Encoder[List[T]], decItem: Decoder[T]) extends Http4sDsl[IO] {

  val crudService = HttpService[IO] {
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

    case req@POST -> Root / Endpoint =>
      for {
        item <- req.decodeJson[T]
        createdItem <- dao.create(item)
        getResult <- dao.get(createdItem.id.get)
        response <- result(getResult)
      } yield response

    case req@PUT -> Root / Endpoint / LongVar(id) =>
      for {
        item <- req.decodeJson[T]
        updateResult <- dao.update(id, item)
        response <- result(updateResult)
      } yield response

    case DELETE -> Root / Endpoint / LongVar(id) =>
      dao.delete(id).flatMap {
        case Left(NotFoundError) => NotFound()
        case Right(_) => NoContent()
      }
  }

  private def result(result: Either[NotFoundError.type, T]) = {
    result match {
      case Left(NotFoundError) => NotFound()
      case Right(item) => Ok(item.asJson)
    }
  }
}
