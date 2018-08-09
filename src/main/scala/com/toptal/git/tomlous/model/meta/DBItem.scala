package com.toptal.git.tomlous.model.meta

import java.time.Instant

trait DBItem {
  def id: Option[Long]
  def created: Option[Instant]
}
