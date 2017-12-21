package com.gluegadget

import org.scalatest.prop.{CommonGenerators, Generator, Randomizer, SizeParam}
import skuber.{Container, ObjectMeta, Pod, Volume}
import skuber.apps.Deployment

object SkuberGen {
  val storageMediumGenerator: Generator[Volume.StorageMedium] = (szp: SizeParam, edges: List[Volume.StorageMedium], rnd: Randomizer) => {
    edges match {
      case head :: tail =>
        (head, tail, rnd)
      case _ =>
        val storageMediums = List[Volume.StorageMedium](Volume.DefaultStorageMedium, Volume.MemoryStorageMedium)
        val (nextInt, nextRandomizer) = rnd.chooseInt(0, 1)
        val medium = storageMediums(nextInt)

        (medium, Nil, nextRandomizer)
    }
  }

  val objectMetaGenerator: Generator[ObjectMeta] = for {
    labels <- CommonGenerators.maps[String, String]
    annotations <- CommonGenerators.maps[String, String]
  } yield ObjectMeta(labels = labels, annotations = annotations)

  implicit val keyToPathGenerator: Generator[Volume.KeyToPath] = for {
    key <- CommonGenerators.strings
    path <- CommonGenerators.strings
  } yield Volume.KeyToPath(key, path)

  val emptyDirGenerator: Generator[Volume.EmptyDir] = for {
    medium <- storageMediumGenerator
  } yield Volume.EmptyDir(medium)

  val optionalStrings = Generator.optionGenerator[String]
  val listKeyPath = Generator.listGenerator[Volume.KeyToPath]

  val gitRepoGenerator: Generator[Volume.GitRepo] = for {
    repository <- CommonGenerators.strings
    revision <- optionalStrings
  } yield Volume.GitRepo(repository, revision)

  val secretGenerator: Generator[Volume.Secret] = for {
    secretName <- CommonGenerators.strings
    items <- Generator.optionGenerator(listKeyPath)
  } yield Volume.Secret(secretName, items)

  val resourceFieldSelector: Generator[Volume.ResourceFieldSelector] = for {
    containerName <- Generator.optionGenerator(CommonGenerators.strings)
    resource <- CommonGenerators.strings
  } yield Volume.ResourceFieldSelector(containerName, None, resource)

  val objectFieldSelector: Generator[Volume.ObjectFieldSelector] = for {
    apiVersion <- CommonGenerators.strings
    fieldPath <- CommonGenerators.strings
  } yield Volume.ObjectFieldSelector(apiVersion, fieldPath)

  val downwardApiVolumeFile: Generator[Volume.DownwardApiVolumeFile] = for {
    fieldRef <- objectFieldSelector
    mode <- Generator.optionGenerator(CommonGenerators.ints)
    path <- CommonGenerators.strings
    resourceFieldRef <- Generator.optionGenerator(resourceFieldSelector)
  } yield Volume.DownwardApiVolumeFile(fieldRef, mode, path, resourceFieldRef)

  val configMapVolumeSource: Generator[Volume.ConfigMapVolumeSource] = for {
    name <- CommonGenerators.strings
    items <- listKeyPath
  } yield Volume.ConfigMapVolumeSource(name, items)

  val downwardApiVolumeSource: Generator[Volume.DownwardApiVolumeSource] = for {
    defaultMode <- Generator.optionGenerator(CommonGenerators.ints)
    items <- Generator.listGenerator(downwardApiVolumeFile)
  } yield Volume.DownwardApiVolumeSource(defaultMode, items)

  val persistentVolumeClaimRef: Generator[Volume.PersistentVolumeClaimRef] = for {
    claimName <- CommonGenerators.strings
    readOnly <- Generator.intGenerator.map(_ % 2 == 0)
  } yield Volume.PersistentVolumeClaimRef(claimName, readOnly)

  val volumeSourceGenerator: Generator[Volume.Source] = CommonGenerators.evenly(emptyDirGenerator, gitRepoGenerator, secretGenerator,
    downwardApiVolumeSource, configMapVolumeSource, persistentVolumeClaimRef)

  val volumeMountGenerator: Generator[Volume.Mount] = for {
    name <- CommonGenerators.strings
    mountPath <- CommonGenerators.strings
  } yield Volume.Mount(name = name, mountPath = mountPath)

  implicit val volumeGenerator: Generator[Volume] = for {
    name <- CommonGenerators.strings
    source <- volumeSourceGenerator
  } yield Volume(name, source)

  implicit val containerGenerator: Generator[Container] = for {
    name <- CommonGenerators.strings
    image <- CommonGenerators.strings
    volumeMounts <- Generator.listGenerator(volumeMountGenerator)
  } yield Container(name = name, image = image, volumeMounts = volumeMounts)

  val podSpecGenerator: Generator[Pod.Spec] = for {
    containers <- Generator.listGenerator[Container]
    volumes <- Generator.listGenerator[Volume]
  } yield Pod.Spec(containers = containers, volumes = volumes)

  val podTemplateSpeccGenerator: Generator[Pod.Template.Spec] = for {
    spec <- Generator.optionGenerator(podSpecGenerator)
  } yield Pod.Template.Spec(spec = spec)

  val specGenerator: Generator[Deployment.Spec] = for {
    replicas <- Generator.optionGenerator(CommonGenerators.posIntValues)
    template <- Generator.optionGenerator(podTemplateSpeccGenerator)
  } yield Deployment.Spec(replicas = replicas, template = template)

  implicit val deploymentGenerator: Generator[Deployment] = for {
    spec <- Generator.optionGenerator(specGenerator)
  } yield Deployment(spec = spec)
}

