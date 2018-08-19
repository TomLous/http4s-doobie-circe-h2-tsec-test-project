package com.toptal.git.tomlous.auth

import cats.effect.IO
import com.toptal.git.tomlous.config.AuthConfig
import com.toptal.git.tomlous.dao.UserDAO
import com.toptal.git.tomlous.model._
import tsec.authentication.{BearerTokenAuthenticator, SecuredRequestHandler, TSecBearerToken}
import tsec.authorization.BasicRBAC

case class AuthedServiceHandler(userDAO: UserDAO, tokenStore: BearerTokenBackingStore, authConfig: AuthConfig) {
  val userStore = UserBackingStore(userDAO)
  val securedRequestHandler = SecuredRequestHandler(BearerTokenAuthenticator(tokenStore, userStore, authConfig.tokenSettings))

  val everyOne = BasicRBAC.all[IO, UserRole, User, TSecBearerToken[Long]]
}
