package com.gluegadget

import org.scalatest.{Matchers, PropSpec}
import org.scalatest.prop.GeneratorDrivenPropertyChecks

abstract class ObjectPropertyChecks extends PropSpec with GeneratorDrivenPropertyChecks with Matchers with SwaggerChecker with ValidationResultValues
