package com.gluegadget

import Validations._
import com.wix.accord._
import skuber.{Pod, Volume}
import skuber.apps.Deployment

object Main extends App {
  val volume = Volume("", Volume.EmptyDir())

  val spec = Pod.Spec(volumes = List(volume))

  val template = Pod.Template.Spec(spec = Some(spec))

  val deploymentSpec = Deployment.Spec(template = Some(template))

  val deployment = Deployment(spec = Some(deploymentSpec))

  println(validate(deployment))
}
