package com.gluegadget

import de.leanovate.swaggercheck.schema.model.{ValidationFailure, ValidationResult, ValidationSuccess}
import org.scalactic.source.Position
import org.scalatest.exceptions.{StackDepthException, TestFailedException}

trait ValidationResultValues {

  import scala.language.implicitConversions

  implicit def convertValidationResultToValidOrInvalid(theValidationResult: ValidationResult)(implicit pos: org.scalactic.source.Position): ValidOrInvalid=
    new ValidOrInvalid(theValidationResult, pos)

  class ValidOrInvalid(theValidationResult: ValidationResult, pos: Position) {
    def invalid: ValidationFailure = {
      theValidationResult match {
        case failure: ValidationFailure => failure
        case _ => throw new TestFailedException((_: StackDepthException) =>
          Some("The ValidationResult on which invalid was invoked was not a failure."), None, pos)
      }
    }

    def valid: ValidationSuccess.type = {
      theValidationResult match {
        case ValidationFailure(e) =>
          throw new TestFailedException((_: StackDepthException) => e.headOption, e.headOption.map(new Exception(_)), pos)
        case _ => ValidationSuccess
      }
    }
  }

}

object ValidationResultValues extends ValidationResultValues

