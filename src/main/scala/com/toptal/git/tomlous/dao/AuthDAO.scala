package com.toptal.git.tomlous.dao

import cats.effect.IO
import com.toptal.git.tomlous.dao.error.DAOErrors._
import com.toptal.git.tomlous.model.{User, UserRole}
import doobie._
import doobie.implicits._
import doobie.util.transactor.Transactor
import org.http4s.BasicCredentials

case class AuthDAO(transactor: Transactor[IO]){

  private implicit val userRoleMeta: Meta[UserRole] = Meta[String].xmap(UserRole.unsafeFromString, _.role)


  def login(credentials: BasicCredentials): IO[Option[User]] = {
    sql"SELECT id, role, username, password, created FROM User WHERE username = ${credentials.username} AND password = HASH('SHA256', STRINGTOUTF8(${credentials.password}), 1000)".query[User].option.transact(transactor)
  }
}