package com.gluegadget

import play.api.libs.json.Json
import skuber.apps.Deployment
import skuber.json.apps.format.depFormat

class DeploymentSpecification extends ObjectPropertyChecks {
  import SkuberGen._

  val definition: String = "io.k8s.kubernetes.pkg.apis.apps.v1beta1.Deployment"

  property("Verifier") {
      val verifier = checker.jsonVerifier(definition)

      forAll { (d: Deployment) =>
        val json = Json.stringify(Json.toJson(d))

        verifier.verify(json).valid.isSuccess shouldBe true
      }
    }
}
