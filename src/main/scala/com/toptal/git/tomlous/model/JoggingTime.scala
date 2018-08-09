package com.toptal.git.tomlous.model

import java.time._

import com.toptal.git.tomlous.model.meta.DBItem

case class JoggingTime(
                      override val id: Option[Long],
                      datetime: LocalDateTime,
                      distance: Double,
                      duration: LocalTime,
                      location: String,
                      weather: Option[String],
                      override val created: Option[Instant]
                      ) extends DBItem
