# Assay

## Properties
Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**name** | [**kotlin.String**](.md) | A name given to the assay | 
**description** | [**kotlin.String**](.md) | General description of the assay. Corresponds to the Protocol REF in ISA |  [optional]
**moisturePercentage** | [**kotlin.String**](.md) | Percentage of moisture in the original sample |  [optional]
**fatPercentage** | [**kotlin.String**](.md) | Percentage of fat in the original sample |  [optional]
**detectionLimit** | [**kotlin.String**](.md) | Limit of detection reported in the unit specified by the variable &#x27;Hazard unit&#x27; |  [optional]
**quantificationLimit** | [**kotlin.String**](.md) | Limit of quantification reported in the unit specified by the variable &#x27;Hazard unit&#x27; |  [optional]
**leftCensoredData** | [**kotlin.String**](.md) | Percentage of measures equal to LOQ and/or LOD |  [optional]
**contaminationRange** | [**kotlin.String**](.md) | Range of result of the analytical measure reported in the unit specified by the variable &#x27;Hazard unit&#x27; before censored data treatment |  [optional]
**uncertaintyValue** | [**kotlin.String**](.md) | Indicate the expanded uncertainty (usually 95% confidence interval) value associated with the measurement expressed in the unit reported in the field &#x27;Hazard unit&#x27; |  [optional]
