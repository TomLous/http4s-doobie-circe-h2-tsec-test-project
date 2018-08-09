package com.toptal.git.tomlous.model

import java.time.Instant

import com.toptal.git.tomlous.model.meta.DBItem

case class User(
                 override val id: Option[Long],
                 role: UserRole,
                 username: String,
                 password: String,
                 override val created: Option[Instant]
               ) extends DBItem
