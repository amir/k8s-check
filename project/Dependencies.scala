import sbt._

object Dependencies {
  val jacksonVersion = "2.9.2"

  lazy val jacksonCore = "com.fasterxml.jackson.core" % "jackson-core" % jacksonVersion
  lazy val jacksonDatabind = "com.fasterxml.jackson.core" % "jackson-databind" % jacksonVersion
  lazy val jacksonScala = "com.fasterxml.jackson.module" %% "jackson-module-scala" % jacksonVersion
  lazy val jacksonYaml = "com.fasterxml.jackson.dataformat" % "jackson-dataformat-yaml" % jacksonVersion
  lazy val scalacheck = "org.scalacheck" %% "scalacheck" % "1.13.5"
  lazy val playWS = "com.typesafe.play" %% "play-ws" % "2.6.7"
  lazy val skuber = "io.skuber" %% "skuber" % "2.0.0-RC2"
  lazy val diffson = "org.gnieh" %% f"diffson-play-json" % "2.2.4"
  lazy val accord = "com.wix" %% "accord-core" % "0.7.1"
}
