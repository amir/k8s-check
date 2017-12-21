package com.gluegadget

import java.io.{File, FileInputStream}

import de.leanovate.swaggercheck.SwaggerChecks
import de.leanovate.swaggercheck.schema.SwaggerAPI
import gnieh.diffson.Pointer
import gnieh.diffson.playJson._
import play.api.libs.json._

sealed trait Identifier {
  def name: String
  def pattern: String
  def maxLength: Int
}

case object DnsLabel extends Identifier {
  val name = "DNS_LABEL"
  val pattern = "^[a-z0-9][-a-z0-9]{0,61}[a-z0-9]$"
  val maxLength = 63
}

case object DnsSubdomain extends Identifier {
  val name = "DNS_SUBDOMAIN"
  val pattern = "^[a-z0-9]([-a-z0-9]*[a-z0-9])?(\\.[a-z0-9]([-a-z0-9]*[a-z0-9])?)*$"
  val maxLength = 253
}

case object PortName extends Identifier {
  val name = "PORT_NAME"
  val pattern = ""
  val maxLength = 15
}

case class PathIdentifier(path: JsPath, identifier: Identifier)

object Enrich {
  private def findPaths(str: String, path: JsPath, value: JsValue, acc: List[JsPath] = List.empty[JsPath]): List[JsPath] = {
    value match {
      case o: JsObject =>
        o.fields.flatMap { e =>
          acc ++ findPaths(str, path \ e._1, e._2, acc)
        }.toList
      case JsString(s) if s.contains(str) => path :: acc
      case JsArray(a) =>
        a.zipWithIndex.flatMap { e=>
          acc ++ findPaths(str, path(e._2), e._1, acc)
        }.toList
      case _ => acc
    }
  }

  val ids: List[Identifier] = List(DnsLabel, DnsSubdomain, PortName)

  def enrich(f: File): String = {
    val is = new FileInputStream(f)
    val json = Json.parse(is)

    val pis = ids.flatMap { i =>
      findPaths(i.name, JsPath, json).flatMap { p: JsPath =>
        val rootPointer = p.path.init.map(_.toString.drop(1))
        val patternPointer = Pointer(rootPointer :+ "pattern" : _*)
        val maxLengthPointer = Pointer(rootPointer :+ "maxLength" : _*)
        List(
          Add(patternPointer, JsString(i.pattern)),
          Add(maxLengthPointer, JsNumber(i.maxLength))
        )
      }
    }

    val patch = JsonPatch(pis)

    patch(json).toString
  }
}