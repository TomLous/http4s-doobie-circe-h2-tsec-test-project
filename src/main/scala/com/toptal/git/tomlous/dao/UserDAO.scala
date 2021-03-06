package com.toptal.git.tomlous.dao

import java.sql._
import java.time.ZoneOffset.UTC
import java.time._

import cats.effect.IO
import com.toptal.git.tomlous.dao.error.DAOErrors._
import com.toptal.git.tomlous.dao.meta.CrudDAO
import com.toptal.git.tomlous.dao.meta.MetaConfig.UserMetaConfig
import com.toptal.git.tomlous.model.{Password, User, UserRole}
import doobie._
import doobie.implicits._
import doobie.util.transactor.Transactor

case class UserDAO(transactor: Transactor[IO]) extends CrudDAO[User] with UserMetaConfig {

  //  implicit val han = LogHandler.jdkLogHandler

  def list: IO[List[User]] = {
    sql"SELECT id, role, username, password, created FROM User".query[User].to[List].transact(transactor)
  }

  def get(id: Long): IO[Either[DAOError, User]] = {
    sql"SELECT id, role, username, password, created FROM User WHERE id = $id".query[User].option.transact(transactor).map {
      case Some(user) => Right(user)
      case None => Left(NotFoundError)
    }
  }

  def create(user: User): IO[Either[DAOError, User]] = {
    (fr"INSERT INTO User (role, username, password ) VALUES (${user.role}, ${user.username}, " ++ sqlPasswordFunction(user.password) ++ fr")")
      .update
      .withUniqueGeneratedKeys[Long]("id")
      .attemptSomeSqlState {
        case SqlState("23505") => UniqueConstraintError
        case state => CustomError(state)
      }.transact(transactor)
      .map {
        case Right(id) => Right(user.copy(id = Some(id)))
        case Left(error) => Left(error)
      }
  }

  def delete(id: Long): IO[Either[DAOError, Unit]] = {
    sql"DELETE FROM User WHERE id = $id".update.run.transact(transactor).map { affectedRows =>
      if (affectedRows == 1) {
        Right(())
      } else {
        Left(NotFoundError)
      }
    }
  }

  def update(id: Long, user: User): IO[Either[DAOError, User]] = {
    (fr"UPDATE User SET role = ${user.role}, username = ${user.username}, password = " ++ sqlPasswordFunction(user.password) ++ fr"WHERE id = $id").update.run.transact(transactor).map { affectedRows =>
      if (affectedRows == 1) {
        Right(user.copy(id = Some(id)))
      } else {
        Left(NotFoundError)
      }
    }
  }

}
