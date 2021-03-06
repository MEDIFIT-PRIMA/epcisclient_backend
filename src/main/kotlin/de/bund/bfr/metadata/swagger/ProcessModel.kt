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

import de.bund.bfr.metadata.swagger.Model
import de.bund.bfr.metadata.swagger.PredictiveModelDataBackground
import de.bund.bfr.metadata.swagger.PredictiveModelGeneralInformation
import de.bund.bfr.metadata.swagger.PredictiveModelModelMath
import de.bund.bfr.metadata.swagger.ProcessModelScope

/**
 * 
 * @param modelType 
 * @param generalInformation 
 * @param scope 
 * @param dataBackground 
 * @param modelMath 
 */
data class ProcessModel (

    val modelType: kotlin.String,
    val generalInformation: PredictiveModelGeneralInformation? = null,
    val scope: ProcessModelScope? = null,
    val dataBackground: PredictiveModelDataBackground? = null,
    val modelMath: PredictiveModelModelMath? = null
) {
}