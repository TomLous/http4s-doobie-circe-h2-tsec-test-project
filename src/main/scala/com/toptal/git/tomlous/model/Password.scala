package com.toptal.git.tomlous.model

import io.circe._

case class Password(value: String) extends AnyVal

object Password {
  // Don't expose password to API (each json will have password filtered out)
  implicit val encodePassword: Encoder[Password] =
    Encoder.encodeString.contramap[Password](_ => "[filtered]")
  implicit val decodePassword: Decoder[Password] = Decoder.decodeString.map(Password(_))

}
