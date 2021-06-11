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

import de.bund.bfr.metadata.swagger.GenericModelDataBackground
import de.bund.bfr.metadata.swagger.GenericModelGeneralInformation
import de.bund.bfr.metadata.swagger.GenericModelModelMath
import de.bund.bfr.metadata.swagger.GenericModelScope
import de.bund.bfr.metadata.swagger.Model

/**
 * 
 * @param modelType 
 * @param generalInformation 
 * @param scope 
 * @param dataBackground 
 * @param modelMath 
 */
data class GenericModel (

    val modelType: kotlin.String,
    val generalInformation: GenericModelGeneralInformation? = null,
    val scope: GenericModelScope? = null,
    val dataBackground: GenericModelDataBackground? = null,
    val modelMath: GenericModelModelMath? = null
) {
}