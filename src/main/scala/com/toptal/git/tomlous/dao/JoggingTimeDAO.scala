package com.toptal.git.tomlous.dao


import java.sql._
import java.time._
import java.time.ZoneOffset.UTC

import cats.effect.IO
import com.toptal.git.tomlous.dao.error.DAOErrors._
import com.toptal.git.tomlous.dao.meta.CrudDAO
import com.toptal.git.tomlous.dao.meta.MetaConfig.JoggingTimeMetaConfig
import com.toptal.git.tomlous.model.JoggingTime
import doobie.util.transactor.Transactor
import fs2.Stream
import doobie._
import doobie.implicits._

case class JoggingTimeDAO(transactor: Transactor[IO]) extends CrudDAO[JoggingTime] with JoggingTimeMetaConfig{

//  implicit val han = LogHandler.jdkLogHandler

  def list: IO[List[JoggingTime]] = {
    sql"SELECT id, datetime, distance, duration, location, weather, created FROM JoggingTime".query[JoggingTime].to[List].transact(transactor)
  }


  def get(id: Long): IO[Either[DAOError, JoggingTime]] = {
    sql"SELECT id, datetime, distance, duration, location, weather, created FROM JoggingTime WHERE id = $id".query[JoggingTime].option.transact(transactor).map {
      case Some(joggingTime) => Right(joggingTime)
      case None => Left(NotFoundError)
    }
  }

  def create(joggingTime: JoggingTime): IO[Either[DAOError, JoggingTime]] = {
    sql"INSERT INTO JoggingTime (datetime, distance, duration, location, weather) VALUES (${joggingTime.datetime}, ${joggingTime.distance}, ${joggingTime.duration}, ${joggingTime.location}, ${joggingTime.weather})"
      .update
      .withUniqueGeneratedKeys[Long]("id")
      .attemptSomeSqlState{
        case SqlState("23505") => UniqueConstraintError
        case state => CustomError(state)
      }
      .transact(transactor)
      .map {
        case Right(id) => Right(joggingTime.copy(id = Some(id)))
        case Left(error) => Left(error)
      }
  }

  def delete(id: Long): IO[Either[DAOError, Unit]] = {
    sql"DELETE FROM JoggingTime WHERE id = $id".update.run.transact(transactor).map { affectedRows =>
      if (affectedRows == 1) {
        Right(())
      } else {
        Left(NotFoundError)
      }
    }
  }

  def update(id: Long, joggingTime: JoggingTime): IO[Either[DAOError, JoggingTime]] = {
    sql"UPDATE JoggingTime SET datetime = ${joggingTime.datetime}, distance = ${joggingTime.distance}, duration = ${joggingTime.duration}, location = ${joggingTime.location}, weather= ${joggingTime.weather} WHERE id = $id".update.run.transact(transactor).map { affectedRows =>
      if (affectedRows == 1) {
        Right(joggingTime.copy(id = Some(id)))
      } else {
        Left(NotFoundError)
      }
    }
  }
}
