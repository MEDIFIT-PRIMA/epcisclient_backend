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

import de.bund.bfr.metadata.swagger.DataModelGeneralInformation
import de.bund.bfr.metadata.swagger.DataModelModelMath
import de.bund.bfr.metadata.swagger.GenericModelDataBackground
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
data class DataModel (

    val modelType: kotlin.String,
    val generalInformation: DataModelGeneralInformation? = null,
    val scope: GenericModelScope? = null,
    val dataBackground: GenericModelDataBackground? = null,
    val modelMath: DataModelModelMath? = null
) {
}