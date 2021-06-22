package com.github.medifitprima.epcisclient

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.node.ArrayNode
import com.fasterxml.jackson.databind.node.ObjectNode
import com.fasterxml.jackson.datatype.threetenbp.ThreeTenModule
import com.fasterxml.jackson.module.kotlin.KotlinModule
import java.time.LocalDate

fun main() {

    val mapper = ObjectMapper().registerModule(KotlinModule()).registerModule(ThreeTenModule())

    val metadataFile = object {}.javaClass.getResource("/metaData.json")
    metadataFile?.let {
        val metadata = mapper.readTree(metadataFile)
        val medifitMetadata = createMedifitMetadata(metadata, mapper)
        mapper.writerWithDefaultPrettyPrinter().writeValue(System.out, medifitMetadata)
    }
}

fun createMedifitMetadata(fskmlMetadata: JsonNode, mapper: ObjectMapper): JsonNode {

    val objectNode = mapper.createObjectNode()

    // fsk:modelType
    val modelType = fskmlMetadata["modelType"]
    objectNode.put("fsk:modelType", convertModelType(modelType.textValue()))

    when (modelType.textValue()) {
        "genericModel" -> {
            objectNode.set<ObjectNode>("fsk:generalInformation", convertGeneralInformation(fskmlMetadata["generalInformation"], mapper))
            objectNode.set<ObjectNode>("fsk:scope", convertGenericModelScope(fskmlMetadata["scope"], mapper))
            objectNode.set<ObjectNode>("fsk:dataBackground", convertGenericModelDataBackground(fskmlMetadata["dataBackground"], mapper))
            objectNode.set<ObjectNode>("fsk:modelMath", convertGenericModelModelMath(fskmlMetadata["modelMath"], mapper))
        }
        "dataModel" -> {
            objectNode.set<ObjectNode>("fsk:generalInformation", convertDataModelGeneralInformation(fskmlMetadata["generalInformation"], mapper))
                .set<ObjectNode>("fsk:scope", convertGenericModelScope(fskmlMetadata["scope"], mapper))
                .set<ObjectNode>("fsk:dataBackground", convertGenericModelDataBackground(fskmlMetadata["dataBackground"], mapper))
                .set<ObjectNode>("fsk:modelMath", convertDataModelModelMath(fskmlMetadata["modelMath"], mapper))
        }
        "predictiveModel" -> {
            objectNode.set<ObjectNode>("fsk:generalInformation", convertPredictiveModelGeneralInformation(fskmlMetadata["generalInformation"], mapper))
                .set<ObjectNode>("fsk:scope", convertPredictiveModelScope(fskmlMetadata["scope"], mapper))
                .set<ObjectNode>("fsk:dataBackground", convertPredictiveModelDataBackground(fskmlMetadata["dataBackground"], mapper))
                .set<ObjectNode>("fsk:modelMath", convertPredictiveModelModelMath(fskmlMetadata["modelMath"], mapper))
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

private fun convertGeneralInformation(originalNode: JsonNode, mapper: ObjectMapper): JsonNode {

    val newNode = mapper.createObjectNode()
    originalNode.copyStringChild("name", newNode, "fsk:name")
    originalNode.copyStringChild("source", newNode, "fsk:source")
    originalNode.copyStringChild("identifier", newNode, "fsk:identifier")

    if (originalNode.has("author")) {
        val newAuthors = newNode.putArray("fsk:author")
        originalNode["author"].map { convertContact(it, mapper) }.forEach(newAuthors::add)
    }

    if (originalNode.has("creator")) {
        val newCreators = newNode.putArray("fsk:author")
        originalNode["creator"].map { convertContact(it, mapper) }.forEach(newCreators::add)
    }

    // TODO: creationDate (LocalDate)
    // TODO: modificationDate (LocalDate[])

    originalNode.copyStringChild("rights", newNode, "fsk:rights")
    originalNode.copyStringChild("availability", newNode, "fsk:rights")
    originalNode.copyStringChild("url", newNode, "fsk:url")
    originalNode.copyStringChild("format", newNode, "fsk:format")

    if (originalNode.has("reference")) {
        val newArray = newNode.putArray("fsk:reference")
        originalNode["reference"].map { convertReference(it, mapper) }.forEach(newArray::add)
    }

    originalNode.copyStringChild("language", newNode, "fsk:language")
    originalNode.copyStringChild("status", newNode, "fsk:status")
    originalNode.copyStringChild("objective", newNode, "fsk:objective")
    originalNode.copyStringChild("description", newNode, "fsk:description")

    return newNode
}

private fun convertDataModelGeneralInformation(originalNode: JsonNode, mapper: ObjectMapper): JsonNode {

    val newNode = mapper.createObjectNode()

    originalNode.copyStringChild("name", newNode, "fsk:name")
    originalNode.copyStringChild("source", newNode, "fsk:source")
    originalNode.copyStringChild("identifier", newNode, "fsk:identifier")

    if (originalNode.has("author")) {
        val newAuthors = newNode.putArray("fsk:author")
        originalNode["author"].map { convertContact(it, mapper) }.forEach(newAuthors::add)
    }

    if (originalNode.has("creator")) {
        val newCreators = newNode.putArray("fsk:creator")
        originalNode["creator"].map { convertContact(it, mapper) }.forEach(newCreators::add)
    }

    // TODO: creationDate (LocalDate)
    // TODO: modificationDate (LocalDate[])

    originalNode.copyStringChild("rights", newNode, "fsk:rights")
    originalNode.copyStringChild("availability", newNode, "fsk:rights")
    originalNode.copyStringChild("url", newNode, "fsk:url")
    originalNode.copyStringChild("format", newNode, "fsk:format")

    if (originalNode.has("reference")) {
        val newArray = newNode.putArray("fsk:reference")
        originalNode["reference"].map { convertReference(it, mapper) }.forEach(newArray::add)
    }

    originalNode.copyStringChild("language", newNode, "fsk:language")
    originalNode.copyStringChild("status", newNode, "fsk:status")
    originalNode.copyStringChild("objective", newNode, "fsk:objective")
    originalNode.copyStringChild("description", newNode, "fsk:description")

    return newNode
}

/**
 * Convert FSK-Lab #/definitions/GenericModelScope
 */
private fun convertGenericModelScope(originalNode: JsonNode, mapper: ObjectMapper): JsonNode {

    val newNode = mapper.createObjectNode()

    if (originalNode.has("product")) {
        val newProducts = newNode.putArray("fsk:product")
        originalNode["product"].map { convertProduct(it, mapper) }.forEach(newProducts::add)
    }

    if (originalNode.has("hazard")) {
        val newHazards = newNode.putArray("fsk:hazard")
        originalNode["hazard"].map { convertHazard(it, mapper) }.forEach(newHazards::add)
    }

    originalNode.copyStringChild("generalComment", newNode, "fsk:generalComment")
    originalNode.copyStringChild("temporalInformation", newNode, "fsk:temporalInformation")

//    "spatialInformation": {
//        "type": "array",
//        "items": {
//        "type": "string"
//    }
//    }

    // TODO: spatialInformation

    return newNode
}

/**
 * Convert FSK-Lab #/definitions/GenericModelDataBackground
 */
private fun convertGenericModelDataBackground(originalNode: JsonNode, mapper: ObjectMapper): JsonNode {

    val newNode = mapper.createObjectNode()
    newNode.set<ObjectNode>("fsk:study", convertStudy(originalNode["study"], mapper))

    if (originalNode.has("studySample")) {
        val newSamples = newNode.putArray("fsk:studySample")
        originalNode["studySample"].map { convertStudySample(it, mapper) }.forEach(newSamples::add)
    }

    if (originalNode.has("laboratory")) {
        val newLabs = newNode.putArray("fsk:laboratory")
        originalNode["laboratory"].map { convertLaboratory(it, mapper) }.forEach(newLabs::add)
    }

    if (originalNode.has("assay")) {
        val newAssays = newNode.putArray("fsk:assay")
        originalNode["assay"].map { convertAssay(it, mapper) }.forEach(newAssays::add)
    }

    return newNode
}

private fun convertStudy(originalNode: JsonNode, mapper: ObjectMapper): JsonNode {

    val newNode = mapper.createObjectNode()
    originalNode.copyStringChild("identifier", newNode, "fsk:identifier")
    originalNode.copyStringChild("title", newNode, "fsk:title")
    originalNode.copyStringChild("description", newNode, "fsk:description")
    originalNode.copyStringChild("designType", newNode, "fsk:designType")
    originalNode.copyStringChild("assayMeasurementType", newNode, "fsk:assayMeasurementType")
    originalNode.copyStringChild("assayTechnologyType", newNode, "fsk:assayTechnologyType")
    originalNode.copyStringChild("assayTechnologyPlatform", newNode, "fsk:assayTechnologyPlatform")
    originalNode.copyStringChild("accreditationProcedureForTheAssayTechnology", newNode,
        "fsk:accreditationProcedureForTheAssayTechnology")
    originalNode.copyStringChild("protocolName", newNode, "fsk:protocolName")
    originalNode.copyStringChild("protocolType", newNode, "fsk:protocolType")
    originalNode.copyStringChild("protocolDescription", newNode, "fsk:protocolDescription")
    originalNode.copyStringChild("protocolURI", newNode, "fsk:protocolURI")
    originalNode.copyStringChild("protocolVersion", newNode, "fsk:protocolVersion")
    originalNode.copyStringChild("protocolParametersName", newNode, "fsk:protocolParametersName")
    originalNode.copyStringChild("protocolComponentsName", newNode, "fsk:protocolComponentsName")
    originalNode.copyStringChild("protocolComponentsType", newNode, "fsk:protocolComponentsType")

    return newNode
}

private fun convertStudySample(originalNode: JsonNode, mapper: ObjectMapper): JsonNode {
    val newNode = mapper.createObjectNode()
    originalNode.copyStringChild("sampleName", newNode, "fsk:sampleName")
    originalNode.copyStringChild("protocolOfSampleCollection", newNode, "fsk:protocolOfSampleCollection")
    originalNode.copyStringChild("samplingStrategy", newNode, "fsk:samplingStrategy")
    originalNode.copyStringChild("typeOfSamplingProgram", newNode, "fsk:typeOfSamplingProgram")
    originalNode.copyStringChild("samplingMethod", newNode, "fsk:samplingMethod")
    originalNode.copyStringChild("samplingPlan", newNode, "fsk:samplingPlan")
    originalNode.copyStringChild("samplingWeight", newNode, "fsk:samplingWeight")
    originalNode.copyStringChild("samplingSize", newNode, "fsk:samplingSize")
    originalNode.copyStringChild("lotSizeUnit", newNode, "fsk:lotSizeUnit")
    originalNode.copyStringChild("samplingPoint", newNode, "fsk:samplingPoint")

    return newNode
}

/**
 * Convert FSK-Lab #/definitions/GenericModelModelMath.
 */
private fun convertGenericModelModelMath(originalNode: JsonNode, mapper: ObjectMapper): JsonNode {

    val newNode = mapper.createObjectNode()
    // TODO: implement convertModelMath
    return newNode
}

private fun convertDataModelModelMath(originalNode: JsonNode, mapper: ObjectMapper): JsonNode {

    val newNode = mapper.createObjectNode()

    if (originalNode.has("parameter")) {
        val newParameters = newNode.putArray("fsk:parameter")
        originalNode["parameter"].map { convertParameter(it, mapper) }.forEach(newParameters::add)
    }

    return newNode
}

private fun convertReference(originalNode: JsonNode, mapper: ObjectMapper): JsonNode {

    val newNode = mapper.createObjectNode()
    originalNode.copyBooleanChild("isReferenceDescription", newNode, "fsk:isReferenceDescription")
    originalNode.copyStringChild("publicationType", newNode, "fsk:publicationType")
    originalNode.copyDateChild("date", newNode, "fsk:date")
    originalNode.copyStringChild("pmid", newNode, "fsk:pmid")
    originalNode.copyStringChild("doi", newNode, "fsk:doi")
    originalNode.copyStringChild("authorList", newNode, "fsk:authorList")
    originalNode.copyStringChild("title", newNode, "fsk:title")
    originalNode.copyStringChild("abstract", newNode, "fsk:abstract")
    originalNode.copyStringChild("journal", newNode, "fsk:journal")
    originalNode.copyStringChild("volume", newNode, "fsk:volume")
    originalNode.copyStringChild("issue", newNode, "fsk:issue")
    originalNode.copyStringChild("status", newNode, "fsk:status")
    originalNode.copyStringChild("website", newNode, "fsk:website")
    originalNode.copyStringChild("comment", newNode, "fsk:comment")

    return newNode
}

private fun convertContact(originalNode: JsonNode, mapper: ObjectMapper): JsonNode {

    val newNode = mapper.createObjectNode()
    originalNode.copyStringChild("title", newNode, "fsk:title")
    originalNode.copyStringChild("familyName", newNode, "fsk:familyName")
    originalNode.copyStringChild("givenName", newNode, "fsk:givenName")
    originalNode.copyStringChild("email", newNode, "fsk:email")
    originalNode.copyStringChild("telephone", newNode, "fsk:telephone")
    originalNode.copyStringChild("streetAddress", newNode, "fsk:streetAddress")
    originalNode.copyStringChild("country", newNode, "fsk:country")
    originalNode.copyStringChild("zipCode", newNode, "fsk:zipCode")
    originalNode.copyStringChild("region", newNode, "fsk:region")
    originalNode.copyStringChild("timeZone", newNode, "fsk:timeZone")
    originalNode.copyStringChild("gender", newNode, "fsk:gender")
    originalNode.copyStringChild("note", newNode, "fsk:note")
    originalNode.copyStringChild("organization", newNode, "fsk:organization")

    return newNode
}

private fun convertProduct(originalNode: JsonNode, mapper: ObjectMapper): JsonNode {

    val newNode = mapper.createObjectNode()
    originalNode.copyStringChild("name", newNode, "fsk:name")
    originalNode.copyStringChild("description", newNode, "fsk:description")
    originalNode.copyStringChild("unit", newNode, "fsk:unit")

    // FIXME: string arrays are not working with MEDIFIT API
//    if (originalNode.has("method")) {
//        newNode.set<ObjectNode>("fsk:method", originalNode["method"])
//    }
//
//    if (originalNode.has("packaging")) {
//        newNode.set<ObjectNode>("fsk:packaging", originalNode["packaging"])
//    }
//
//    if (originalNode.has("treatment")) {
//        newNode.set<ObjectNode>("fsk:treatment", originalNode["treatment"])
//    }

    originalNode.copyStringChild("originCountry", newNode, "fsk:originCountry")
    originalNode.copyStringChild("originArea", newNode, "fsk:originArea")
    originalNode.copyStringChild("fisheriesArea", newNode, "fsk:fisheriesArea")
    originalNode.copyDateChild("productionDate", newNode, "fsk:productionDate")
    originalNode.copyDateChild("expiryDate", newNode, "fsk:expiryDate")

    return newNode
}

private fun convertHazard(originalNode: JsonNode, mapper: ObjectMapper): JsonNode {

    val newNode = mapper.createObjectNode()
    originalNode.copyStringChild("type", newNode, "fsk:type")
    originalNode.copyStringChild("name", newNode, "fsk:name")
    originalNode.copyStringChild("description", newNode, "fsk:description")
    originalNode.copyStringChild("unit", newNode, "fsk:unit")
    originalNode.copyStringChild("adverseEffect", newNode, "fsk:adverseEffect")
    originalNode.copyStringChild("sourceOfContamination", newNode, "fsk:sourceOfContamination")
    originalNode.copyStringChild("benchmarkDose", newNode, "fsk:benchmarkDose")
    originalNode.copyStringChild("maximumResidueLimit", newNode, "fsk:maximumResidueLimit")
    originalNode.copyStringChild("noObservedAdverseAffectLevel", newNode, "fsk:noObservedAdverseAffectLevel")
    originalNode.copyStringChild("lowestObservedAdverseAffectLevel", newNode, "fsk:lowestObservedAdverseAffectLevel")
    originalNode.copyStringChild("acceptableOperatorsExposureLevel", newNode, "fsk:acceptableOperatorsExposureLevel")
    originalNode.copyStringChild("acuteReferenceDose", newNode, "fsk:acuteReferenceDose")
    originalNode.copyStringChild("acceptableDailyIntake", newNode, "fsk:acceptableDailyIntake")
    originalNode.copyStringChild("indSum", newNode, "fsk:indSum")

    return newNode
}

private fun convertLaboratory(originalNode: JsonNode, mapper: ObjectMapper): JsonNode {

    val newNode = mapper.createObjectNode()
    // FIXME: string arrays are not working with MEDIFIT API
//    newNode.set<ObjectNode>("fsk:accreditation", originalNode["accreditation"])
    originalNode.copyStringChild("name", newNode, "fsk:name")
    originalNode.copyStringChild("country", newNode, "fsk:country")

    return newNode
}

private fun convertAssay(originalNode: JsonNode, mapper: ObjectMapper): JsonNode {

    val newNode = mapper.createObjectNode()
    originalNode.copyStringChild("name", newNode, "fsk:name")
    originalNode.copyStringChild("description", newNode, "fsk:description")
    originalNode.copyStringChild("moisturePercentage", newNode, "fsk:moisturePercentage")
    originalNode.copyStringChild("fatPercentage", newNode, "fsk:fatPercentage")
    originalNode.copyStringChild("detectionLimit", newNode, "fsk:detectionLimit")
    originalNode.copyStringChild("quantificationLimit", newNode, "fsk:quantificationLimit")
    originalNode.copyStringChild("leftCensoredData", newNode, "fsk:leftCensoredData")
    originalNode.copyStringChild("contaminationRange", newNode, "fsk:contaminationRange")
    originalNode.copyStringChild("uncertaintyValue", newNode, "fsk:uncertaintyValue")

    return newNode
}

private fun convertParameter(originalNode: JsonNode, mapper: ObjectMapper): JsonNode {

    val newNode = mapper.createObjectNode()
    originalNode.copyStringChild("id", newNode, "fsk:id")
    originalNode.copyStringChild("classification", newNode, "fsk:classification")
    originalNode.copyStringChild("name", newNode, "fsk:name")
    originalNode.copyStringChild("description", newNode, "fsk:description")
    originalNode.copyStringChild("unit", newNode, "fsk:unit")
    originalNode.copyStringChild("unitCategory", newNode, "fsk:unitCategory")
    originalNode.copyStringChild("dataType", newNode, "fsk:dataType")
    originalNode.copyStringChild("source", newNode, "fsk:source")
    originalNode.copyStringChild("subject", newNode, "fsk:subject")
    originalNode.copyStringChild("distribution", newNode, "fsk:distribution")
    originalNode.copyStringChild("value", newNode, "fsk:value")
    // TODO: reference
    originalNode.copyStringChild("variabilitySubject", newNode, "fsk:variabilitySubject")
    originalNode.copyStringChild("minValue", newNode, "fsk:minValue")
    originalNode.copyStringChild("maxValue", newNode, "fsk:maxValue")
    originalNode.copyStringChild("error", newNode, "fsk:error")

    return newNode
}

private fun convertPredictiveModelGeneralInformation(originalNode: JsonNode, mapper: ObjectMapper): JsonNode {
    val newNode = mapper.createObjectNode()

    originalNode.copyStringChild("name", newNode, "fsk:name")
    originalNode.copyStringChild("source", newNode, "fsk:source")
    originalNode.copyStringChild("identifier", newNode, "fsk:identifier")

    if (originalNode.has("author")) {
        val newAuthors = newNode.putArray("fsk:author")
        originalNode["author"].map { convertContact(it, mapper) }.forEach(newAuthors::add)
    }

    if (originalNode.has("creator")) {
        val newCreators = newNode.putArray("fsk:creator")
        originalNode["creator"].map { convertContact(it, mapper) }.forEach(newCreators::add)
    }

    // TODO: creationDate (LocalDate)
    // TODO: modificationDate (LocalDate[])

    originalNode.copyStringChild("rights", newNode, "fsk:rights")
    originalNode.copyStringChild("availability", newNode, "fsk:availability")
    originalNode.copyStringChild("url", newNode, "fsk:url")
    originalNode.copyStringChild("format", newNode, "fsk:format")

    if (originalNode.has("reference")) {
        val newReferences = newNode.putArray("fsk:reference")
        originalNode["reference"].map { convertContact(it, mapper) }.forEach(newReferences::add)
    }

    originalNode.copyStringChild("language", newNode, "fsk:language")
    originalNode.copyStringChild("software", newNode, "fsk:software")
    originalNode.copyStringChild("languageWrittenIn", newNode, "fsk:languageWrittenIn")

    // TODO: modelCategory

    originalNode.copyStringChild("status", newNode, "fsk:status")
    originalNode.copyStringChild("objective", newNode, "fsk:objective")
    originalNode.copyStringChild("description", newNode, "fsk:description")

    return newNode
}

private fun convertPredictiveModelScope(originalNode: JsonNode, mapper: ObjectMapper): JsonNode {
    val newNode = mapper.createObjectNode()

    if (originalNode.has("product")) {
        val newProducts = newNode.putArray("fsk:product")
        originalNode["product"].map { convertProduct(it, mapper) }.forEach(newProducts::add)
    }

    if (originalNode.has("hazard")) {
        val newHazards = newNode.putArray("fsk:hazard")
        originalNode["hazard"].map { convertHazard(it, mapper) }.forEach(newHazards::add)
    }

    originalNode.copyStringChild("generalComment", newNode, "fsk:generalComment")
    originalNode.copyStringChild("temporalInformation", newNode, "fsk:temporalInformation")

    // TODO: spatial information

    return newNode
}

private fun convertPredictiveModelDataBackground(originalNode: JsonNode, mapper: ObjectMapper): JsonNode {

    val newNode = mapper.createObjectNode()

    newNode.set<ObjectNode>("fsk:study", convertStudy(originalNode["study"], mapper))

    if (originalNode.has("studySample")) {
        val newSamples = newNode.putArray("fsk:studySample")
        originalNode["studySample"].map { convertStudySample(it, mapper) }.forEach(newSamples::add)
    }

    if (originalNode.has("laboratory")) {
        val newLabs = newNode.putArray("fsk:laboratory")
        originalNode["laboratory"].map { convertLaboratory(it, mapper) }.forEach(newLabs::add)
    }

    if (originalNode.has("assay")) {
        val newAssays = newNode.putArray("fsk:assay")
        originalNode["assay"].map { convertAssay(it, mapper) }.forEach(newAssays::add)
    }

    return newNode
}

private fun convertPredictiveModelModelMath(originalNode: JsonNode, mapper: ObjectMapper): JsonNode {

    val newNode = mapper.createObjectNode()

    if (originalNode.has("parameter")) {
        val newParameters = newNode.putArray("fsk:parameter")
        originalNode["parameter"].map { convertParameter(it, mapper) }.forEach(newParameters::add)
    }

    if (originalNode.has("qualityMeasures")) {
        val newMeasures = newNode.putArray("fsk:qualityMeasures")
        originalNode["qualityMeasures"].map { convertQualityMeasures(it, mapper) }.forEach(newMeasures::add)
    }

    if (originalNode.has("modelEquation")) {
        val newEquations = newNode.putArray("modelEquation")
        originalNode["qualityMeasures"].map { convertModelEquation(it, mapper) }.forEach(newEquations::add)
    }

    originalNode.copyStringChild("fittingProcedure", newNode, "fsk:fittingProcedure")

    if (originalNode.has("event")) {
        newNode.set<ObjectNode>("event", originalNode["event"])
    }

    return newNode
}

private fun convertQualityMeasures(originalNode: JsonNode, mapper: ObjectMapper): JsonNode {

    val newNode = mapper.createObjectNode()
    originalNode.copyNumericalValue("sse", newNode, "fsk:sse")
    originalNode.copyNumericalValue("mse", newNode, "fsk:mse")
    originalNode.copyNumericalValue("rmse", newNode, "fsk:rmse")
    originalNode.copyNumericalValue("rsquared", newNode, "fsk:rsquared")
    originalNode.copyNumericalValue("aic", newNode, "fsk:aic")
    originalNode.copyNumericalValue("bic", newNode, "fsk:bic")
    originalNode.copyStringChild("sensitivityAnalysis", newNode, "fsk:sensitivityAnalysis")

    return newNode
}

private fun convertModelEquation(originalNode: JsonNode, mapper: ObjectMapper): JsonNode {

    val newNode = mapper.createObjectNode()

    originalNode.copyStringChild("name", newNode, "fsk:name")
    originalNode.copyStringChild("modelEquationClass", newNode, "fsk:modelEquationClass")
    // TODO: reference
    originalNode.copyStringChild("modelEquation", newNode, "fsk:modelEquation")

    if (originalNode.has("modelHypothesis")) {
        newNode.set<ObjectNode>("fsk:modelHypothesis", originalNode["modelHypothesis"])
    }

    return newNode
}

private fun JsonNode.copyStringChild(originalKey: String, newNode: ObjectNode, newKey: String) {
    this[originalKey]?.let { newNode.put(newKey, it.textValue()) }
}

private fun JsonNode.copyBooleanChild(originalKey: String, newNode: ObjectNode, newKey: String) {
    this[originalKey]?.let { newNode.put(newKey, it.asText()) }
}

private fun JsonNode.copyNumericalValue(originalKey: String, newNode: ObjectNode, newKey: String) {
    this[originalKey]?.let { newNode.put(newKey, it.asText()) }
}

private fun JsonNode.copyDateChild(originalKey: String, newNode: ObjectNode, newKey: String) {
    if (has(originalKey)) {
        val originalDate = this[originalKey] as ArrayNode
        val localDate = LocalDate.of(originalDate[0].intValue(), originalDate[1].intValue(), originalDate[2].intValue())
        newNode.put(newKey, localDate.toString())
    }
}
