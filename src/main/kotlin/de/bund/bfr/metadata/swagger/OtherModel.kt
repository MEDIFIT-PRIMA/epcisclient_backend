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
import de.bund.bfr.metadata.swagger.OtherModelDataBackground
import de.bund.bfr.metadata.swagger.OtherModelGeneralInformation
import de.bund.bfr.metadata.swagger.OtherModelModelMath
import de.bund.bfr.metadata.swagger.OtherModelScope

/**
 * 
 * @param modelType 
 * @param generalInformation 
 * @param scope 
 * @param dataBackground 
 * @param modelMath 
 */
data class OtherModel (

    val modelType: kotlin.String,
    val generalInformation: OtherModelGeneralInformation? = null,
    val scope: OtherModelScope? = null,
    val dataBackground: OtherModelDataBackground? = null,
    val modelMath: OtherModelModelMath? = null
) {
}