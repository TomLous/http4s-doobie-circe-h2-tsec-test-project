package com.toptal.git.tomlous.dao


import java.sql._
import java.time._
import java.time.ZoneOffset.UTC

import cats.effect.IO
import com.toptal.git.tomlous.dao.error.NotFoundError
import com.toptal.git.tomlous.model.JoggingTime
import doobie.util.transactor.Transactor
import fs2.Stream
import doobie._
import doobie.implicits._

case class JoggingTimeDAO(transactor: Transactor[IO]) {

  private implicit val localTimeMeta: Meta[LocalTime] = Meta[Time].xmap(_.toLocalTime, Time.valueOf)
  private implicit val localDateTimeMeta: Meta[LocalDateTime] = Meta[Timestamp].xmap(ts => LocalDateTime.ofInstant(ts.toInstant, UTC), ldt =>  new Timestamp(ldt.toInstant(UTC).toEpochMilli))


  def list: IO[List[JoggingTime]] = {
    sql"SELECT id, datetime, distance, duration, location, weather, created FROM JoggingTime".query[JoggingTime].to[List].transact(transactor)
  }


  def get(id: Long): IO[Either[NotFoundError.type, JoggingTime]] = {
    sql"SELECT id, datetime, distance, duration, location, weather, created FROM JoggingTime WHERE id = $id".query[JoggingTime].option.transact(transactor).map {
      case Some(joggingTime) => Right(joggingTime)
      case None => Left(NotFoundError)
    }
  }

  def create(joggingTime: JoggingTime): IO[JoggingTime] = {
    sql"INSERT INTO JoggingTime (datetime, distance, duration, location, weather) VALUES (${joggingTime.datetime}, ${joggingTime.distance}, ${joggingTime.duration}, ${joggingTime.location}, ${joggingTime.weather})".update.withUniqueGeneratedKeys[Long]("id").transact(transactor).map { id =>
      joggingTime.copy(id = Some(id))
    }
  }

  def delete(id: Long): IO[Either[NotFoundError.type, Unit]] = {
    sql"DELETE FROM JoggingTime WHERE id = $id".update.run.transact(transactor).map { affectedRows =>
      if (affectedRows == 1) {
        Right(())
      } else {
        Left(NotFoundError)
      }
    }
  }

  def update(id: Long, joggingTime: JoggingTime): IO[Either[NotFoundError.type, JoggingTime]] = {
    sql"UPDATE JoggingTime SET datetime = ${joggingTime.datetime}, distance = ${joggingTime.distance}, duration = ${joggingTime.duration}, location = ${joggingTime.location}, weather= ${joggingTime.weather} WHERE id = $id".update.run.transact(transactor).map { affectedRows =>
      if (affectedRows == 1) {
        Right(joggingTime.copy(id = Some(id)))
      } else {
        Left(NotFoundError)
      }
    }
  }
}
