package com.toptal.git.tomlous.dao.error

import doobie.enum.SqlState
import org.http4s.Status

object DAOErrors {

  abstract class DAOError(val message:String){}

  object NotFoundError extends DAOError("Not Found")

  object UniqueConstraintError extends DAOError("Duplicate data")

  case class CustomError(state: SqlState) extends DAOError("SQL error: " + state.value)

}