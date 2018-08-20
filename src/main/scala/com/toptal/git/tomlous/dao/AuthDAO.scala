package com.toptal.git.tomlous.dao

import cats.effect.IO
import com.toptal.git.tomlous.dao.meta.MetaConfig.UserMetaConfig
import com.toptal.git.tomlous.model._
import com.typesafe.scalalogging.LazyLogging
import doobie._
import doobie.implicits._
import doobie.util.transactor.Transactor
import org.http4s.BasicCredentials

case class AuthDAO(transactor: Transactor[IO]) extends UserMetaConfig with LazyLogging{

//  implicit val han = LogHandler.jdkLogHandler

  def login(credentials: BasicCredentials): IO[Option[User]] = {
    logger.debug(s"Login ${credentials.username}")
    (fr"SELECT id, role, username, password, created FROM User WHERE username = ${credentials.username} AND password = " ++ sqlPasswordFunction(credentials.password))
      .query[User]
      .option
      .transact(transactor)
  }
}