package com.gluegadget

import org.scalatest.prop.{CommonGenerators, Generator, Randomizer, SizeParam}
import skuber.Volume

object SkuberGen {
  import CommonGenerators._

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

  implicit val keyToPathGenerator: Generator[Volume.KeyToPath] = for {
    key <- strings
    path <- strings
  } yield Volume.KeyToPath(key, path)

  val emptyDirGenerator: Generator[Volume.EmptyDir] = for {
    medium <- storageMediumGenerator
  } yield Volume.EmptyDir(medium)

  val optionalStrings = Generator.optionGenerator[String]
  val listKeyPath = Generator.listGenerator[Volume.KeyToPath]

  val gitRepoGenerator: Generator[Volume.GitRepo] = for {
    repository <- strings
    revision <- optionalStrings
  } yield Volume.GitRepo(repository, revision)

  val secretGenerator: Generator[Volume.Secret] = for {
    secretName <- strings
    items <- Generator.optionGenerator(listKeyPath)
  } yield Volume.Secret(secretName, items)

  val resourceFieldSelector: Generator[Volume.ResourceFieldSelector] = for {
    containerName <- Generator.optionGenerator(strings)
    resource <- strings
  } yield Volume.ResourceFieldSelector(containerName, None, resource)

  val objectFieldSelector: Generator[Volume.ObjectFieldSelector] = for {
    apiVersion <- strings
    fieldPath <- strings
  } yield Volume.ObjectFieldSelector(apiVersion, fieldPath)

  val downwardApiVolumeFile: Generator[Volume.DownwardApiVolumeFile] = for {
    fieldRef <- objectFieldSelector
    mode <- Generator.optionGenerator(ints)
    path <- strings
    resourceFieldRef <- Generator.optionGenerator(resourceFieldSelector)
  } yield Volume.DownwardApiVolumeFile(fieldRef, mode, path, resourceFieldRef)

  val configMapVolumeSource: Generator[Volume.ConfigMapVolumeSource] = for {
    name <- strings
    items <- listKeyPath
  } yield Volume.ConfigMapVolumeSource(name, items)

  val downwardApiVolumeSource: Generator[Volume.DownwardApiVolumeSource] = for {
    defaultMode <- Generator.optionGenerator(ints)
    items <- Generator.listGenerator(downwardApiVolumeFile)
  } yield Volume.DownwardApiVolumeSource(defaultMode, items)

  val persistentVolumeClaimRef: Generator[Volume.PersistentVolumeClaimRef] = for {
    claimName <- strings
    readOnly <- Generator.intGenerator.map(_ % 2 == 0)
  } yield Volume.PersistentVolumeClaimRef(claimName, readOnly)

  val volumeSourceGenerator: Generator[Volume.Source] = evenly(emptyDirGenerator, gitRepoGenerator, secretGenerator,
    downwardApiVolumeSource, configMapVolumeSource, persistentVolumeClaimRef)

  implicit val volumeGenerator: Generator[Volume] = for {
    name <- strings
    source <- volumeSourceGenerator
  } yield Volume(name, source)
}

