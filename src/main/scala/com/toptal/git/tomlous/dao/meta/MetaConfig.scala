package com.toptal.git.tomlous.dao.meta

import java.sql.{Time, Timestamp}
import java.time.{LocalDateTime, LocalTime}
import java.time.ZoneOffset.UTC

import com.toptal.git.tomlous.model.{Password, UserRole}
import doobie._
import doobie.implicits._


object MetaConfig {

  trait JoggingTimeMetaConfig {
    implicit val localTimeMeta: Meta[LocalTime] = Meta[Time].xmap(_.toLocalTime, Time.valueOf)
    implicit val localDateTimeMeta: Meta[LocalDateTime] = Meta[Timestamp].xmap(ts => LocalDateTime.ofInstant(ts.toInstant, UTC), ldt =>  new Timestamp(ldt.toInstant(UTC).toEpochMilli))
  }

  trait UserMetaConfig{
    implicit val userRoleMeta: Meta[UserRole] = Meta[String].xmap(UserRole.unsafeFromString, _.role)
    implicit val passwordMeta: Meta[Password] = Meta[String].xmap[Password](x => Password(x), _.value)

    def sqlPasswordFunction(password:String):Fragment = fr"HASH('SHA256', STRINGTOUTF8($password), 1000)"
    def sqlPasswordFunction(password:Option[Password]):Fragment = sqlPasswordFunction(password.map(_.value).getOrElse(""))

  }

}
