package com.toptal.git.tomlous.dao.meta

import cats.effect.IO
import com.toptal.git.tomlous.dao.error.DAOErrors._
import com.toptal.git.tomlous.model.meta.DBItem

trait CrudDAO[T <: DBItem] {

  def list: IO[List[T]]

  def get(id: Long): IO[Either[DAOError, T]]

  def create(item: T):  IO[Either[DAOError, T]]

  def delete(id: Long): IO[Either[DAOError, Unit]]

  def update(id: Long, item: T): IO[Either[DAOError, T]]
}
