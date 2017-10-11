import Dependencies._

lazy val root = (project in file(".")).settings(inThisBuild(List(
  organization := "com.gluegadget",
  scalaVersion := "2.11.11",
  version      := "0.1.0-SNAPSHOT"
)),
  name := "k8s-check",
  libraryDependencies ++= Seq(
    playWS,
    jacksonCore,
    jacksonScala,
    jacksonYaml,
    jacksonDatabind,
    scalacheck,
  )
)
