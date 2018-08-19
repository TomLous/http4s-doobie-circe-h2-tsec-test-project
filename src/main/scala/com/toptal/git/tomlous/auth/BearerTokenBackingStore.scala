package com.toptal.git.tomlous.auth

import cats.effect.IO
import tsec.authentication.{BackingStore, TSecBearerToken}
import tsec.common.SecureRandomId

abstract class BearerTokenBackingStore extends BackingStore[IO, SecureRandomId, TSecBearerToken[Long]]{

  def getId(token: TSecBearerToken[Long]): SecureRandomId

}
