package com.toptal.git.tomlous.model

import java.time._

case class JoggingTime(
                      id: Option[Long],
                      datetime: LocalDateTime,
                      distance: Double,
                      duration: LocalTime,
                      location: String,
                      weather: Option[String],
                      created: Option[Instant]
                      )
