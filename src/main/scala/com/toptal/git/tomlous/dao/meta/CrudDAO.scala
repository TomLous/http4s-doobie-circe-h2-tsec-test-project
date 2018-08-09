package com.toptal.git.tomlous.dao.meta

import cats.effect.IO
import com.toptal.git.tomlous.dao.error.NotFoundError
import com.toptal.git.tomlous.model.meta.DBItem

trait CrudDAO[T <: DBItem] {

  def list: IO[List[T]]

  def get(id: Long): IO[Either[NotFoundError.type, T]]

  def create(item: T): IO[T]

  def delete(id: Long): IO[Either[NotFoundError.type, Unit]]

  def update(id: Long, item: T): IO[Either[NotFoundError.type, T]]
}
