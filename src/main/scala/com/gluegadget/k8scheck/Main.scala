package com.gluegadget.k8scheck

import java.io.{File, FileInputStream}
import play.api.libs.json.Json

import skuber._
import skuber.apps._
import skuber.json.format._
import skuber.json.apps.format._

import de.leanovate.swaggercheck.SwaggerChecks
import de.leanovate.swaggercheck.schema.SwaggerAPI

object Main {
  def main(args: Array[String]): Unit = {
    val filename: String = "swagger.json"

    val definition: String = "io.k8s.kubernetes.pkg.apis.apps.v1beta1.Deployment"
    val swaggerAPI = SwaggerAPI.parse(new FileInputStream(new File(filename)))
    println(swaggerAPI.definitions.get(definition))

    val swaggerChecks = SwaggerChecks(new FileInputStream(new File(filename)))
    val gen = swaggerChecks.jsonGenerator(definition)
    println(gen)
    Range(0, 100).foreach { idx =>
      val d = gen.sample.map(_.prettyfied).map(j => Json.parse(j).as[Deployment])
      println(s"[$idx] $d")
    }
  }
}
