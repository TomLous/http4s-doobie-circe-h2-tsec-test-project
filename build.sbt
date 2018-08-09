lazy val commonSettings = Seq(
  name := "toptal-project",
  version := "0.0.1-SNAPSHOT",
  scalaVersion := "2.12.6"
)

val http4sVersion = "0.18.14"
val circeVersion = "0.9.3"
val doobieVersion = "0.5.3"
val specs2Version = "4.2.0"
val logbackVersion = "1.2.3"
val pureConfigVersion = "0.9.1"
val h2Version = "1.4.192"
val scalaTestVersion = "3.0.4"
val scalaMockVersion = "4.0.0"
val flywayVersion = "5.1.4"
val tsecVersion = "0.0.1-M11"

lazy val root = (project in file("."))
  .configs(IntegrationTest)
  .settings(
    commonSettings,
    Defaults.itSettings,
    libraryDependencies ++= Seq(
      "org.http4s" %% "http4s-blaze-server" % http4sVersion,
      "org.http4s" %% "http4s-circe" % http4sVersion,
      "org.http4s" %% "http4s-dsl" % http4sVersion,
      "org.http4s" %% "http4s-blaze-server" % http4sVersion,
      "org.http4s" %% "http4s-blaze-client" % http4sVersion % "it,test",
      "org.http4s" %% "http4s-circe" % http4sVersion,

      "io.circe" %% "circe-generic" % circeVersion,
      "io.circe" %% "circe-java8" % circeVersion,
      "io.circe" %% "circe-literal" % circeVersion % "it,test",
      "io.circe" %% "circe-optics" % circeVersion % "it",

      "com.github.pureconfig" %% "pureconfig" % pureConfigVersion,

      "org.tpolecat" %% "doobie-core" % doobieVersion,
      "org.tpolecat" %% "doobie-h2" % doobieVersion,
      "org.tpolecat" %% "doobie-hikari" % doobieVersion,

      "com.h2database" % "h2" % h2Version,

      "org.flywaydb" % "flyway-core" % flywayVersion,

      "org.scalatest" %% "scalatest" % scalaTestVersion % "it,test",
      "org.scalamock" %% "scalamock" % scalaMockVersion % "test",

      "ch.qos.logback" % "logback-classic" % logbackVersion,

      "io.github.jmcardon" %% "tsec-common" % tsecVersion,
      "io.github.jmcardon" %% "tsec-password" % tsecVersion,
      "io.github.jmcardon" %% "tsec-mac" % tsecVersion,
      "io.github.jmcardon" %% "tsec-signatures" % tsecVersion,
      "io.github.jmcardon" %% "tsec-jwt-mac" % tsecVersion,
      "io.github.jmcardon" %% "tsec-jwt-sig" % tsecVersion,
      "io.github.jmcardon" %% "tsec-http4s" % tsecVersion
    )
  )


scalacOptions ++= Seq("-Ypartial-unification")
addCompilerPlugin("org.scalamacros" % "paradise" % "2.1.0" cross CrossVersion.full)
