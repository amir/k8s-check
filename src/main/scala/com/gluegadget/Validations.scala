package com.gluegadget

import com.wix.accord.dsl._
import skuber.{Pod, Volume}
import skuber.apps.Deployment

object Validations {

  implicit val volumeValidator = validator[Volume] { v =>
    v.name is notEmpty
  }

  implicit val podSpecValidator = validator[Pod.Spec] { s =>
    s.volumes.each is valid
  }

  implicit val podTemplateSpecValidator = validator[Pod.Template.Spec] { s =>
    s.spec.each is valid
  }

  implicit val deploymentSpecValidator = validator[Deployment.Spec] { s =>
    s.template.each is valid
  }

  implicit val deploymentValidator = validator[Deployment] { d =>
    d.spec.each is valid
  }

}
