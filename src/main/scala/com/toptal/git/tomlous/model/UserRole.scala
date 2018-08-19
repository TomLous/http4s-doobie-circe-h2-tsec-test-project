package com.toptal.git.tomlous.model

import cats._
import cats.data.OptionT
import cats.effect.{IO, Sync}
import cats.implicits._
import org.http4s.HttpService
import org.http4s.dsl.io._
import tsec.authentication._
import tsec.authorization._
import tsec.cipher.symmetric.jca._
import tsec.common.SecureRandomId
import tsec.jws.mac.JWTMac

import scala.collection.mutable
import scala.concurrent.duration._
import io.circe._
import tsec.authorization.{AuthGroup, SimpleAuthEnum}

case class UserRole(role: String) extends AnyVal

object UserRole extends SimpleAuthEnum[UserRole, String] {

  implicit val encodeUserRole: Encoder[UserRole] = Encoder.encodeString.contramap[UserRole](_.role)
  implicit val decodeUserRole: Decoder[UserRole] = Decoder.decodeString.map[UserRole](UserRole.unsafeFromString)


  val Admin = UserRole("admin")
  val UserManager = UserRole("userManager")
  val Regular = UserRole("regular")

  private def roles = List("regular","userManager", "admin").map(UserRole(_)).toSet

  implicit val E: Eq[UserRole] = Eq.fromUniversalEquals[UserRole]
  override val getRepr: UserRole => String = (t: UserRole) => t.role

  protected val values: AuthGroup[UserRole] = AuthGroup(Admin, UserManager, Regular)

  override val orElse: UserRole = Regular



  def unsafeFromString(value: String): UserRole = {
    values.find(_.role.toLowerCase == value.toLowerCase).get
  }


}