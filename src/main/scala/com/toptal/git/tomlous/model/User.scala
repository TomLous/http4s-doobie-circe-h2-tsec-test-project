package com.toptal.git.tomlous.model

import java.time.Instant
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
import meta.DBItem

case class User(
                 override val id: Option[Long],
                 role: UserRole,
                 username: String,
                 password: Option[Password],
                 override val created: Option[Instant]
               ) extends DBItem

object User {
  implicit def authRole[F[_]](implicit F: MonadError[F, Throwable]): AuthorizationInfo[F, UserRole, User] =
    new AuthorizationInfo[F, UserRole, User] {
      def fetchInfo(u: User): F[UserRole] = F.pure(u.role)
    }

  /*
  new AuthorizationInfo[F, UserRole, User] {
      def fetchInfo(u: User): F[UserRole] = F.pure(u.role)
    }
   */
}