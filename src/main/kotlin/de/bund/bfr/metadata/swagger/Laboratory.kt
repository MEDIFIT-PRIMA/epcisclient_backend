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


/**
 * 
 * @param accreditation 
 * @param name Laboratory code (National laboratory code if available) or Laboratory name
 * @param country Country where the laboratory is placed. (ISO 3166-1-alpha-2)
 */
data class Laboratory (

    val accreditation: kotlin.Array<kotlin.String>,
    /* Laboratory code (National laboratory code if available) or Laboratory name */
    val name: kotlin.String? = null,
    /* Country where the laboratory is placed. (ISO 3166-1-alpha-2) */
    val country: kotlin.String? = null
) {
}