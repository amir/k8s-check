package com.gluegadget

import java.io.{File, FileInputStream}

import play.api.libs.json.Json
import skuber.apps.Deployment
import skuber.json.apps.format.depFormat
import de.leanovate.swaggercheck.SwaggerChecks
import de.leanovate.swaggercheck.schema.ValidationResultToProp._
import org.scalacheck.{Arbitrary, Gen, Properties}
import org.scalacheck.Prop.forAll
import skuber.ObjectMeta

object DeploymentSpecification extends Properties("Deployment") {
  val swaggerCheck = SwaggerChecks(new FileInputStream(new File("swagger.json")))

  property("Deployment") = {
    val definition: String = "io.k8s.kubernetes.pkg.apis.apps.v1beta1.Deployment"
    val verifier = swaggerCheck.jsonVerifier(definition)

    forAll(Arbitrary.arbitrary[Deployment]) {
      deployment: Deployment =>
        val json = Json.stringify(Json.toJson(deployment))

        verifier.verify(json)
    }
  }

  implicit val depSpecArb = Arbitrary(for {
    rs <- Gen.option(Gen.size)
  } yield Deployment.Spec(replicas = rs))

  implicit val depArb = Arbitrary(for {
    spec <- Gen.option(Arbitrary.arbitrary[Deployment.Spec])
  } yield Deployment(kind = "", apiVersion = "", metadata = ObjectMeta(), spec = spec, status = None))
}
