package com.toptal.git.tomlous.service

import com.toptal.git.tomlous.dao.UserDAO
import com.toptal.git.tomlous.model.{JoggingTime, User, UserRole}
import com.toptal.git.tomlous.service.meta.CrudService
import io.circe.generic.auto._
import cats.effect.IO
import cats.implicits._
import com.toptal.git.tomlous.dao.JoggingTimeDAO

import com.toptal.git.tomlous.service.meta.CrudService
import org.http4s.HttpService
import org.http4s.dsl.Http4sDsl
import org.http4s.circe._
import org.http4s.server.syntax._
import io.circe._
import io.circe.syntax._
import io.circe.generic.auto._
import io.circe.java8.time._
import cats.effect.IO
import org.http4s.{HttpService, MediaType, Uri}
import org.http4s.dsl.Http4sDsl
import org.http4s.circe._

import io.circe.generic.auto._
import io.circe.syntax._
import fs2.Stream
import io.circe.{Decoder, Encoder}
import org.http4s.headers.{Location, `Content-Type`}


case class UserService(dao: UserDAO) extends CrudService[User](dao, "user") {





  val service = crudService
}
