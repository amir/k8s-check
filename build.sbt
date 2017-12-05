import Dependencies._

lazy val root = (project in file(".")).settings(inThisBuild(List(
  organization := "com.gluegadget",
  scalaVersion := "2.12.4",
  version      := "0.1.0-SNAPSHOT"
)),
  name := "k8s-check",
  libraryDependencies ++= Seq(
    playWS,
    skuber,
    jacksonCore,
    jacksonScala,
    jacksonYaml,
    jacksonDatabind,
    scalacheck,
  )
)
