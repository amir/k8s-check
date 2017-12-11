package com.gluegadget

import java.io.File

import de.leanovate.swaggercheck.SwaggerChecks
import de.leanovate.swaggercheck.schema.SwaggerAPI
import org.scalatest._
import org.scalatest.prop.GeneratorDrivenPropertyChecks
import play.api.libs.json.Json
import skuber.Volume
import skuber.json.format.volumeFormat

class VolumeSpecification extends PropSpec with GeneratorDrivenPropertyChecks with Matchers {
  val swagger = new File("swagger.json")
  val enrichedSwagger: String = Enrich.enrich(swagger)

  val swaggerCheck = SwaggerChecks(SwaggerAPI.parse(enrichedSwagger))
  val definition: String = "io.k8s.kubernetes.pkg.api.v1.Volume"

  import SkuberGen._
  import ValidationResultValues._

  property("Verifier") {
    val verifier = swaggerCheck.jsonVerifier(definition)

    forAll { (v: Volume) =>
      val json = Json.stringify(Json.toJson(v))

      verifier.verify(json).valid.isSuccess shouldBe true
    }
  }
}

