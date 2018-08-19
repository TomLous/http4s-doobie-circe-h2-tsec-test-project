package com.toptal.git.tomlous.auth

import cats.data.OptionT
import cats.effect.IO
import tsec.authentication.{BackingStore, TSecBearerToken}
import tsec.common.SecureRandomId

import scala.collection.concurrent.TrieMap

object InMemoryBearerTokenBackingStore extends BearerTokenBackingStore {
  private lazy val storageMap = TrieMap.empty[SecureRandomId, TSecBearerToken[Long]]

  def getId(token: TSecBearerToken[Long]): SecureRandomId = SecureRandomId.coerce(token.id)

  override def put(token: TSecBearerToken[Long]): IO[TSecBearerToken[Long]] = {
    val map = storageMap.put(getId(token), token)
    if (map.isEmpty)
      IO.pure(token)
    else
      IO.raiseError(new IllegalArgumentException)
  }

  override def update(token: TSecBearerToken[Long]): IO[TSecBearerToken[Long]] = {
    storageMap.update(getId(token), token)
    IO.pure(token)
  }

  override def delete(id: SecureRandomId): IO[Unit] = {
    storageMap.remove(id) match {
      case Some(_) => IO.unit
      case None => IO.raiseError(new IllegalArgumentException)

    }
  }

  override def get(id: SecureRandomId): OptionT[IO, TSecBearerToken[Long]] = OptionT.fromOption[IO](storageMap.get(id))


}
