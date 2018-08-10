package com.toptal.git.tomlous.model

import java.time.Instant

import meta.DBItem

case class User(
                 override val id: Option[Long],
                 role: UserRole,
                 username: String,
                 password: String,
                 override val created: Option[Instant]
               ) extends DBItem
