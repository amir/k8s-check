package com.gluegadget

import java.io.File

import play.api.libs.json.{JsSuccess, Json}
import skuber.json.format.volumeFormat
import de.leanovate.swaggercheck.SwaggerChecks
import de.leanovate.swaggercheck.schema.SwaggerAPI
import de.leanovate.swaggercheck.schema.ValidationResultToProp._
import de.leanovate.swaggercheck.shrinkable.CheckJsValue
import org.scalacheck.{Arbitrary, Properties}
import org.scalacheck.Prop.forAll
import skuber.Volume

object VolumeSpecification extends Properties("Volume") {
  import SkuberGen._

  val swagger = new File("swagger.json")
  val enrichedSwagger: String = Enrich.enrich(swagger)

  val swaggerCheck = SwaggerChecks(SwaggerAPI.parse(enrichedSwagger))
  val definition: String = "io.k8s.kubernetes.pkg.api.v1.Volume"

  property("Generator") = {
    implicit val arbitraryJson = Arbitrary[CheckJsValue](swaggerCheck.jsonGenerator(definition))
    forAll { j: CheckJsValue =>
      val JsSuccess(_, p) = Json.parse(j.minified).validate[Volume]

      p.toString() == ""
    }
  }

  property("Verifier") = {
    val verifier = swaggerCheck.jsonVerifier(definition)

    forAll { (v: Volume) =>
      val json = Json.stringify(Json.toJson(v))

      verifier.verify(json)
    }
  }
}

