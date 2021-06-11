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
 * @param name The product, matrix or environment (e.g food product, lab media, soil etc.) for which the model or data applies
 * @param description Detailed description of the product, matrix or environment for which the model or data applies
 * @param unit Unit of the product, matrix or environment for which the model or data applies
 * @param method Type of production for the product/ matrix
 * @param packaging Describe container or wrapper that holds the product/matrix. Common type of packaging (paper or plastic bags, boxes, tinplate or aluminium cans, plastic trays, plastic bottles, glass bottles or jars)
 * @param treatment Used to characterise a product/matrix based on the treatment or processes applied to the product or any indexed ingredient
 * @param originCountry Country of origin of the food/product (ISO 3166-1-alpha-2 country code)
 * @param originArea Area of origin of the food/product (Nomenclature of territorial units for statistics – NUTS – coding system valid only for EEA and Switzerland)
 * @param fisheriesArea Fisheries or aquaculture area specifying the origin of the sample (FAO Fisheries areas)
 * @param productionDate Date of production of food/product
 * @param expiryDate Date of expiry of food/product
 */
data class Product (

    /* The product, matrix or environment (e.g food product, lab media, soil etc.) for which the model or data applies */
    val name: kotlin.String,
    /* Detailed description of the product, matrix or environment for which the model or data applies */
    val description: kotlin.String? = null,
    /* Unit of the product, matrix or environment for which the model or data applies */
    val unit: kotlin.String,
    /* Type of production for the product/ matrix */
    val method: kotlin.Array<kotlin.String>? = null,
    /* Describe container or wrapper that holds the product/matrix. Common type of packaging (paper or plastic bags, boxes, tinplate or aluminium cans, plastic trays, plastic bottles, glass bottles or jars) */
    val packaging: kotlin.Array<kotlin.String>? = null,
    /* Used to characterise a product/matrix based on the treatment or processes applied to the product or any indexed ingredient */
    val treatment: kotlin.Array<kotlin.String>? = null,
    /* Country of origin of the food/product (ISO 3166-1-alpha-2 country code) */
    val originCountry: kotlin.String? = null,
    /* Area of origin of the food/product (Nomenclature of territorial units for statistics – NUTS – coding system valid only for EEA and Switzerland) */
    val originArea: kotlin.String? = null,
    /* Fisheries or aquaculture area specifying the origin of the sample (FAO Fisheries areas) */
    val fisheriesArea: kotlin.String? = null,
    /* Date of production of food/product */
    val productionDate: java.time.LocalDate? = null,
    /* Date of expiry of food/product */
    val expiryDate: java.time.LocalDate? = null
) {
}