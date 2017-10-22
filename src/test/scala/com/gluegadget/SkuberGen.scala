package com.gluegadget

import org.scalacheck.{Arbitrary, Gen}
import skuber.apps.Deployment
import skuber.{Container, ObjectMeta, Pod, Volume}

object SkuberGen {
  def genMap: Gen[Map[String, String]] = for {
    keys <- Gen.containerOf[List, String](Gen.identifier)
    values <- Gen.containerOfN[List, String](keys.size, Gen.identifier)
  } yield (keys zip values).toMap

  implicit val objMetaArb: Arbitrary[ObjectMeta] = Arbitrary(for {
    labels <- SkuberGen.genMap
    annotations <- SkuberGen.genMap
  } yield ObjectMeta(labels = labels, annotations = annotations))

  implicit val volumeArb: Arbitrary[Volume] = Arbitrary(for {
    name <- Gen.identifier
    source <- Gen.oneOf[Volume.Source](Seq(Volume.EmptyDir(), Volume.HostPath(name)))
  } yield Volume(name = name, source = source))

  implicit val volumeMountArb: Arbitrary[Volume.Mount] = Arbitrary(for {
    name <- Gen.identifier
    mountPath <- Gen.identifier
  } yield Volume.Mount(name = name, mountPath = mountPath))

  implicit val containerArb: Arbitrary[Container] = Arbitrary(for {
    name <- Gen.identifier
    image <- Gen.identifier
    volumeMounts <- Gen.containerOf[List, Volume.Mount](Arbitrary.arbitrary[Volume.Mount])
  } yield Container(name = name, image = image, volumeMounts = volumeMounts))

  implicit val podSpecArb: Arbitrary[Pod.Spec] = Arbitrary(for {
    containers <- Gen.containerOf[List, Container](Arbitrary.arbitrary[Container])
    volumes <- Gen.containerOf[List, Volume](Arbitrary.arbitrary[Volume])
  } yield Pod.Spec(containers = containers, volumes = volumes))

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
