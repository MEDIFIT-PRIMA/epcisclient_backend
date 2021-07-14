package com.github.medifitprima.epcisclient

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.node.ObjectNode
import com.github.medifitprima.epcisclient.metadata.*

fun createMedifitMetadata(fskmlMetadata: JsonNode, mapper: ObjectMapper): JsonNode {

    val objectNode = mapper.createObjectNode()

    // fsk:modelType
    val modelType = fskmlMetadata["modelType"]
    objectNode.put("fsk:modelType", convertModelType(modelType.textValue()))

    when (modelType.textValue()) {
        "genericModel" -> {
            val converter = GenericModelConverter()
            objectNode.set<ObjectNode>("fsk:generalInformation", converter.convertGeneralInformation(fskmlMetadata["generalInformation"], mapper))
            objectNode.set<ObjectNode>("fsk:scope", converter.convertScope(fskmlMetadata["scope"], mapper))
            objectNode.set<ObjectNode>("fsk:dataBackground", converter.convertDataBackground(fskmlMetadata["dataBackground"], mapper))
            objectNode.set<ObjectNode>("fsk:modelMath", converter.convertModelMath(fskmlMetadata["modelMath"], mapper))
        }
        "dataModel" -> {
            val converter = DataModelConverter()
            objectNode.set<ObjectNode>("fsk:generalInformation", converter.convertGeneralInformation(fskmlMetadata["generalInformation"], mapper))
                .set<ObjectNode>("fsk:scope", converter.convertScope(fskmlMetadata["scope"], mapper))
                .set<ObjectNode>("fsk:dataBackground", converter.convertDataBackground(fskmlMetadata["dataBackground"], mapper))
                .set<ObjectNode>("fsk:modelMath", converter.convertModelMath(fskmlMetadata["modelMath"], mapper))
        }
        "predictiveModel" -> {
            val converter = PredictiveModelConverter()
            objectNode.set<ObjectNode>("fsk:generalInformation", converter.convertGeneralInformation(fskmlMetadata["generalInformation"], mapper))
                .set<ObjectNode>("fsk:scope", converter.convertScope(fskmlMetadata["scope"], mapper))
                .set<ObjectNode>("fsk:dataBackground", converter.convertDataBackground(fskmlMetadata["dataBackground"], mapper))
                .set<ObjectNode>("fsk:modelMath", converter.convertModelMath(fskmlMetadata["modelMath"], mapper))
        }
        "otherModel" -> {
            val converter = OtherModelConverter()
            objectNode.set<ObjectNode>("fsk:generalInformation", converter.convertGeneralInformation(fskmlMetadata["generalInformation"], mapper))
                .set<ObjectNode>("fsk:scope", converter.convertScope(fskmlMetadata["scope"], mapper))
                .set<ObjectNode>("fsk:dataBackground", converter.convertDataBackground(fskmlMetadata["dataBackground"], mapper))
                .set<ObjectNode>("fsk:modelMath", converter.convertModelMath(fskmlMetadata["modelMath"], mapper))
        }
        "exposureModel" -> {
            val converter = ExposureModelConverter()
            objectNode.set<ObjectNode>("fsk:generalInformation", converter.convertGeneralInformation(fskmlMetadata["generalInformation"], mapper))
                .set<ObjectNode>("fsk:scope", converter.convertScope(fskmlMetadata["scope"], mapper))
                .set<ObjectNode>("fsk:dataBackground", converter.convertDataBackground(fskmlMetadata["dataBackground"], mapper))
                .set<ObjectNode>("fsk:modelMath", converter.convertModelMath(fskmlMetadata["modelMath"], mapper))
        }
    }

    return objectNode
}

private fun convertModelType(originalType: String): String {
    return when (originalType) {
        "genericModel" -> "GenericModel"
        "dataModel" -> "DataModel"
        "predictiveModel" -> "PredictiveModel"
        "otherModel" -> "OtherModel"
        "exposureModel" -> "ExposureModel"
        "toxicologicalModel" -> "ToxicologicalModel"
        "doseResponseModel" -> "DoseResponseModel"
        "consumptionModel" -> "ConsumptionModel"
        "healthModel" -> "HealthModel"
        "riskModel" -> "RiskModel"
        "qraModel" -> "QraModel"
        else -> "GenericModel"
    }
}

/** Utility data class for the API. Instead of sending the complete original metadata only keeps a subset used in the
 * frontend. */
data class ModelView(
    val type: String,
    val name: String,
    val software: String,
    val products: List<String>,
    val hazards: List<String>
)

fun extractModelView(model: JsonNode): ModelView {
    val modelType = model["fsk:modelType"]?.textValue() ?: ""
    var name = ""
    var software = ""
    val products = mutableListOf<String>()
    val hazards = mutableListOf<String>()

    if (model.has("fsk:generalInformation")) {
        val generalInformation = model["fsk:generalInformation"]
        name = generalInformation["fsk:name"]?.textValue() ?: ""
        software = generalInformation["fsk:languageWrittenIn"]?.textValue() ?: ""
    }

    if (model.has("fsk:scope")) {
        val scope = model["fsk:scope"]

        // Get products{
        scope["fsk:product"].map { productNode ->
            if (productNode.has("fsk:name")) {
                products.add(productNode["fsk:name"].textValue())
            }
        }

        // Get hazards
        scope["fsk:hazard"].map { hazardNode ->
            if (hazardNode.has("fsk:name")) {
                hazards.add(hazardNode["fsk:name"].textValue())
            }
        }
    }

    return ModelView(modelType, name, software, products, hazards)
}
