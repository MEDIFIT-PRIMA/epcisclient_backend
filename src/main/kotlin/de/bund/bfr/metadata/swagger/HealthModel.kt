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

import de.bund.bfr.metadata.swagger.GenericModelModelMath
import de.bund.bfr.metadata.swagger.HealthModelScope
import de.bund.bfr.metadata.swagger.Model
import de.bund.bfr.metadata.swagger.PredictiveModelDataBackground
import de.bund.bfr.metadata.swagger.PredictiveModelGeneralInformation

/**
 * 
 * @param modelType 
 * @param generalInformation 
 * @param scope 
 * @param dataBackground 
 * @param modelMath 
 */
data class HealthModel (

    val modelType: kotlin.String,
    val generalInformation: PredictiveModelGeneralInformation? = null,
    val scope: HealthModelScope? = null,
    val dataBackground: PredictiveModelDataBackground? = null,
    val modelMath: GenericModelModelMath? = null
) {
}