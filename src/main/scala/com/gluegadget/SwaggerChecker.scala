package com.gluegadget

import java.io.File

import de.leanovate.swaggercheck.SwaggerChecks
import de.leanovate.swaggercheck.schema.SwaggerAPI

trait SwaggerChecker {
  val checker = SwaggerChecks(SwaggerAPI.parse(Enrich.enrich(new File("swagger.json"))), maxItems = 1000)
}

