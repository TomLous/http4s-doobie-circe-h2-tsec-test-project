package com.toptal.git.tomlous.service.meta

import cats.effect.IO
import cats.data._
import com.toptal.git.tomlous.auth.AuthedServiceHandler
import com.toptal.git.tomlous.dao.error.DAOErrors._
import com.toptal.git.tomlous.dao.meta.CrudDAO
import com.toptal.git.tomlous.model.User
import com.toptal.git.tomlous.model.meta.DBItem
import io.circe._
import io.circe.syntax._
import org.http4s.HttpService
import org.http4s.circe._
import org.http4s.dsl.Http4sDsl
import tsec.authentication._


abstract class  CrudService[T <: DBItem](dao: CrudDAO[T], authedServiceHandler:AuthedServiceHandler)(implicit val encItem: Encoder[T], encList: Encoder[List[T]], decItem: Decoder[T]) extends Http4sDsl[IO] {

  val crudService = TSecAuthService.withAuthorization(authedServiceHandler.everyOne) {
    case GET -> Root asAuthed user =>
      for {
        getResult <- dao.list
        response <- Ok(getResult.asJson)
      } yield response

    case GET -> Root / LongVar(id) asAuthed user =>
      for {
        getResult <- dao.get(id)
        response <- result(getResult)
      } yield response

    case req@POST -> Root asAuthed user =>
      val complex = for {
        item <- EitherT.right(req.request.decodeJson[T])
        createdResult <- EitherT(dao.create(item))
        getResult <- EitherT(dao.get(createdResult.id.get))
      } yield getResult

      for {
        getResult <- complex.value
        response <- result(getResult)
      } yield response

    case req@PUT -> Root / LongVar(id) asAuthed user =>
      for {
        item <- req.request.decodeJson[T]
        updateResult <- dao.update(id, item)
        response <- result(updateResult)
      } yield response

    case DELETE -> Root / LongVar(id) asAuthed user =>
      dao.delete(id).flatMap {
        case Right(_) => NoContent()
        case Left(NotFoundError) => NotFound(NotFoundError.message)
        case Left(c:DAOError) => InternalServerError(c.message)
        case Left(e) => InternalServerError(e.toString)
      }
  }

  def result(result: Either[DAOError, T]) = {
    result match {
      case Left(NotFoundError) => NotFound(NotFoundError.message)
      case Left(UniqueConstraintError) => InternalServerError(UniqueConstraintError.message)
      case Left(c:CustomError) => InternalServerError(c.message)
      case Left(e) => InternalServerError(e.toString)
      case Right(item) => Ok(item.asJson)
    }
  }
}
