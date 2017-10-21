package com.gluegadget

import java.io.{File, FileInputStream}

import play.api.libs.json.{JsSuccess, Json}
import skuber.apps.Deployment
import skuber.json.apps.format.depFormat
import de.leanovate.swaggercheck.SwaggerChecks
import de.leanovate.swaggercheck.schema.ValidationResultToProp._
import de.leanovate.swaggercheck.shrinkable.CheckJsValue
import org.scalacheck.{Arbitrary, Gen, Properties}
import org.scalacheck.Prop.forAll
import skuber.{Container, ObjectMeta, Pod}

object DeploymentSpecification extends Properties("Deployment") {
  val swaggerCheck = SwaggerChecks(new FileInputStream(new File("swagger.json")))
  val definition: String = "io.k8s.kubernetes.pkg.apis.apps.v1beta1.Deployment"

  property("Verifier") = {
    val verifier = swaggerCheck.jsonVerifier(definition)

    forAll { (d: Deployment) =>
      val json = Json.stringify(Json.toJson(d))

      verifier.verify(json)
    }
  }

  property("Generator") = {
    implicit val arbitraryJson = Arbitrary[CheckJsValue](swaggerCheck.jsonGenerator(definition))

    forAll { j: CheckJsValue =>
      val JsSuccess(_, p) = Json.parse(j.minified).validate[Deployment]

      p.toString() == ""
    }
  }

  def genMap: Gen[Map[String, String]] = for {
    keys <- Gen.containerOf[List, String](Gen.identifier)
    values <- Gen.containerOfN[List, String](keys.size, Gen.identifier)
  } yield (keys zip values).toMap

  implicit val objMetaArb: Arbitrary[ObjectMeta] = Arbitrary(for {
    labels <- genMap
    annotations <- genMap
  } yield ObjectMeta(labels = labels, annotations = annotations))

  implicit val containerArb: Arbitrary[Container] = Arbitrary(for {
    name <- Gen.identifier
    image <- Gen.identifier
  } yield Container(name = name, image = image))

  implicit val podSpecArb: Arbitrary[Pod.Spec] = Arbitrary(for {
    containers <- Gen.containerOf[List, Container](Arbitrary.arbitrary[Container])
  } yield Pod.Spec(containers = containers))

  implicit val podTemplateSpecArb: Arbitrary[Pod.Template.Spec] = Arbitrary(for {
    metadata <- Arbitrary.arbitrary[ObjectMeta]
    spec <- Gen.option(Arbitrary.arbitrary[Pod.Spec])
  } yield Pod.Template.Spec(metadata = metadata, spec = spec))

  implicit val depSpecArb: Arbitrary[Deployment.Spec] = Arbitrary(for {
    rs <- Gen.option(Gen.posNum[Int])
    template <- Gen.option(Arbitrary.arbitrary[Pod.Template.Spec])
  } yield Deployment.Spec(replicas = rs, template = template))

  implicit val depStatusArb: Arbitrary[Deployment.Status] = Arbitrary(for {
    rs    <- Gen.posNum[Int]
    urs   <- Gen.posNum[Int]
    ars   <- Gen.posNum[Int]
    unars <- Gen.posNum[Int]
    ors   <- Gen.posNum[Int]
  } yield Deployment.Status(rs, urs, ars, unars, ors))

  implicit val depArb: Arbitrary[Deployment] = Arbitrary(for {
    spec <- Gen.option(Arbitrary.arbitrary[Deployment.Spec])
    status <- Gen.option(Arbitrary.arbitrary[Deployment.Status])
    metadata <- Arbitrary.arbitrary[ObjectMeta]
  } yield Deployment(kind = "", apiVersion = "", metadata = metadata, spec = spec, status = status))
}
