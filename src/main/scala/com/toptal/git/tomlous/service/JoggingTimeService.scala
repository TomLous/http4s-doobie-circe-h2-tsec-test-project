package com.toptal.git.tomlous.service


import cats.effect.IO
import cats.implicits._
import com.toptal.git.tomlous.dao.JoggingTimeDAO
import com.toptal.git.tomlous.dao.error.NotFoundError
import com.toptal.git.tomlous.model.JoggingTime
import com.toptal.git.tomlous.service.meta.CrudService
import org.http4s.HttpService
import org.http4s.dsl.Http4sDsl
import org.http4s.circe._
import org.http4s.server.syntax._
import io.circe._
import io.circe.syntax._
import io.circe.generic.auto._
import io.circe.java8.time._




case class JoggingTimeService (dao: JoggingTimeDAO) extends CrudService[JoggingTime](dao, "joggingtime")  {
  //  private implicit val encodeUserRole: Encoder[UserRole] = Encoder.encodeString.contramap[UserRole](_.value)

  //  private implicit val decodeUserRole: Decoder[UserRole] = Decoder.decodeString.map[UserRole](UserRole.unsafeFromString)




  val service = crudService
}
