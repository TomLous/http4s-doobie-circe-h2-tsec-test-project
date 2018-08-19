package com.toptal.git.tomlous.auth

import cats.data.OptionT
import cats.effect.IO
import com.toptal.git.tomlous.dao.UserDAO
import com.toptal.git.tomlous.dao.error.DAOErrors
import com.toptal.git.tomlous.model.User
import tsec.authentication.BackingStore

case class UserBackingStore(userDAO: UserDAO) extends BackingStore[IO, Long, User] {
  override def put(elem: User): IO[User] =
    userDAO.create(elem).map {
      case Right(user) => user
      case Left(e) => throw new Exception(e.message)
    }


  override def update(v: User): IO[User] =
    userDAO.update(v.id.get, v).map {
      case Right(user) => user
      case Left(e) => throw new Exception(e.message)
    }

  override def delete(id: Long): IO[Unit] =
    userDAO.delete(id).map {
      case Right(u) => u
      case Left(e) => throw new Exception(e.message)
    }

  override def get(id: Long): OptionT[IO, User] =
    OptionT(
      userDAO.get(id).map {
        case Right(user) => Some(user)
        case Left(_) => None
      })
}
