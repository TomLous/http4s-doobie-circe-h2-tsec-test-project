package com.toptal.git.tomlous.dao.error

import doobie.enum.SqlState

object DAOErrors {

  trait DAOError

  object NotFoundError extends DAOError

  object UniqueConstraintError extends DAOError

  case class CustomError(state: SqlState) extends DAOError

}