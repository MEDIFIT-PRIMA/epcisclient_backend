/**
 * RAKIP Generic model
 * TODO
 *
 * OpenAPI spec version: 1.0.4
 * 
 *
 * NOTE: This class is auto generated by the swagger code generator program.
 * https://github.com/swagger-api/swagger-codegen.git
 * Do not edit the class manually.
 */
package de.bund.bfr.metadata.swagger

import de.bund.bfr.metadata.swagger.ModelEquation
import de.bund.bfr.metadata.swagger.Parameter
import de.bund.bfr.metadata.swagger.QualityMeasures

/**
 * 
 * @param parameter 
 * @param qualityMeasures 
 * @param modelEquation 
 * @param fittingProcedure 
 * @param event 
 */
data class OtherModelModelMath (

    val parameter: kotlin.Array<Parameter>,
    val qualityMeasures: kotlin.Array<QualityMeasures>? = null,
    val modelEquation: kotlin.Array<ModelEquation>? = null,
    val fittingProcedure: kotlin.String? = null,
    val event: kotlin.Array<kotlin.String>? = null
) {
}