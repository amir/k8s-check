package com.gluegadget

import java.io.File

import play.api.libs.json.{JsSuccess, Json}
import skuber.apps.Deployment
import skuber.json.apps.format.depFormat
import de.leanovate.swaggercheck.SwaggerChecks
import de.leanovate.swaggercheck.schema.SwaggerAPI
import de.leanovate.swaggercheck.schema.ValidationResultToProp._
import de.leanovate.swaggercheck.shrinkable.CheckJsValue

object DeploymentSpecification {
  import SkuberGen._

  val swagger = new File("swagger.json")
  val enrichedSwagger: String = Enrich.enrich(swagger)

  val swaggerCheck = SwaggerChecks(SwaggerAPI.parse(enrichedSwagger), maxItems = 1000)
  val definition: String = "io.k8s.kubernetes.pkg.apis.apps.v1beta1.Deployment"
}
