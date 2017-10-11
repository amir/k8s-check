import sbt._

object Dependencies {
  val jacksonVersion = "2.8.6"

  lazy val jacksonCore = "com.fasterxml.jackson.core" % "jackson-core" % jacksonVersion
  lazy val jacksonDatabind = "com.fasterxml.jackson.core" % "jackson-databind" % jacksonVersion
  lazy val jacksonScala = "com.fasterxml.jackson.module" %% "jackson-module-scala" % jacksonVersion
  lazy val jacksonYaml = "com.fasterxml.jackson.dataformat" % "jackson-dataformat-yaml" % jacksonVersion
  lazy val scalacheck = "org.scalacheck" %% "scalacheck" % "1.13.4"
  lazy val playWS = "com.typesafe.play" %% "play-ws" % "2.4.8"
}
