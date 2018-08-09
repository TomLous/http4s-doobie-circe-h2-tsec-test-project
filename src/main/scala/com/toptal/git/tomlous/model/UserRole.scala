package com.toptal.git.tomlous.model

abstract sealed class UserRole(val role: String)
object RegularRole extends UserRole("regular")
object UserManagerRole extends UserRole("userManager")
object AdminRole extends UserRole("admin")

object UserRole {
  private def values = Set(RegularRole, UserManagerRole, AdminRole)

  def unsafeFromString(value: String): UserRole = {
    values.find(_.role == value).get
  }
}