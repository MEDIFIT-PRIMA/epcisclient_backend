# Product

## Properties
Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**name** | [**kotlin.String**](.md) | The product, matrix or environment (e.g food product, lab media, soil etc.) for which the model or data applies | 
**description** | [**kotlin.String**](.md) | Detailed description of the product, matrix or environment for which the model or data applies |  [optional]
**unit** | [**kotlin.String**](.md) | Unit of the product, matrix or environment for which the model or data applies | 
**method** | [**kotlin.Array&lt;kotlin.String&gt;**](.md) | Type of production for the product/ matrix |  [optional]
**packaging** | [**kotlin.Array&lt;kotlin.String&gt;**](.md) | Describe container or wrapper that holds the product/matrix. Common type of packaging (paper or plastic bags, boxes, tinplate or aluminium cans, plastic trays, plastic bottles, glass bottles or jars) |  [optional]
**treatment** | [**kotlin.Array&lt;kotlin.String&gt;**](.md) | Used to characterise a product/matrix based on the treatment or processes applied to the product or any indexed ingredient |  [optional]
**originCountry** | [**kotlin.String**](.md) | Country of origin of the food/product (ISO 3166-1-alpha-2 country code) |  [optional]
**originArea** | [**kotlin.String**](.md) | Area of origin of the food/product (Nomenclature of territorial units for statistics – NUTS – coding system valid only for EEA and Switzerland) |  [optional]
**fisheriesArea** | [**kotlin.String**](.md) | Fisheries or aquaculture area specifying the origin of the sample (FAO Fisheries areas) |  [optional]
**productionDate** | [**java.time.LocalDate**](java.time.LocalDate.md) | Date of production of food/product |  [optional]
**expiryDate** | [**java.time.LocalDate**](java.time.LocalDate.md) | Date of expiry of food/product |  [optional]
