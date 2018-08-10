package com.toptal.git.tomlous.service.meta

import cats.effect.IO
import cats.data._
import com.toptal.git.tomlous.dao.error.DAOErrors._
import com.toptal.git.tomlous.dao.meta.CrudDAO
import com.toptal.git.tomlous.model.UserRole

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
      val complex = for {
        item <- EitherT.right(req.decodeJson[T])
        createdResult <- EitherT(dao.create(item))
        getResult <- EitherT(dao.get(createdResult.id.get))
      } yield getResult

      for {
        getResult <- complex.value
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
        case Left(NotFoundError) => NotFound("Not Found")
        case Right(_) => NoContent()
        case Left(e) => InternalServerError(e.toString)
      }
  }

  private def result(result: Either[DAOError, T]) = {
    result match {
      case Left(NotFoundError) => NotFound("Not Found")
      case Left(UniqueConstraintError) => InternalServerError("Duplicate data")
      case Left(CustomError(e)) => InternalServerError(e.value)
      case Left(e) => InternalServerError(e.toString)
      case Right(item) => Ok(item.asJson)
    }
  }
}
