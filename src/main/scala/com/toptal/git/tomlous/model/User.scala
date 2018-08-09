package com.toptal.git.tomlous.model

import java.time.Instant

case class User(
                 id: Option[Long],
                 role: UserRole,
                 username: String,
                 password: String,
                 created: Option[Instant]
               )
