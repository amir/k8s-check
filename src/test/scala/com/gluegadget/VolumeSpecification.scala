package com.gluegadget

import play.api.libs.json.Json
import skuber.Volume
import skuber.json.format.volumeFormat

class VolumeSpecification extends ObjectPropertyChecks {
  import SkuberGen._

  val definition: String = "io.k8s.kubernetes.pkg.api.v1.Volume"

  property("Verifier") {
    val verifier = checker.jsonVerifier(definition)

    forAll { (v: Volume) =>
      val json = Json.stringify(Json.toJson(v))

      verifier.verify(json).valid.isSuccess shouldBe true
    }
  }
}

