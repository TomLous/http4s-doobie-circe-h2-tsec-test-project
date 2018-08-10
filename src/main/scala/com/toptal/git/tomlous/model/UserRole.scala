package com.toptal.git.tomlous.model

import io.circe._

case class UserRole(role: String) extends AnyVal

object UserRole {

  implicit val encodeUserRole: Encoder[UserRole] = Encoder.encodeString.contramap[UserRole](_.role)
  implicit val decodeUserRole: Decoder[UserRole] = Decoder.decodeString.map[UserRole](UserRole.unsafeFromString)

  private def roles = List("regular","userManager", "admin").map(UserRole(_)).toSet

  def unsafeFromString(value: String): UserRole = {
    roles.find(_.role.toLowerCase == value.toLowerCase).get
  }
}