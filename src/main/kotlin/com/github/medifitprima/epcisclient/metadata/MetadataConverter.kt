package com.github.medifitprima.epcisclient.metadata

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.node.ArrayNode
import com.fasterxml.jackson.databind.node.ObjectNode
import java.time.LocalDate

interface MetadataConverter {
    fun convertGeneralInformation(original: JsonNode, mapper: ObjectMapper): JsonNode
    fun convertScope(original: JsonNode, mapper: ObjectMapper): JsonNode
    fun convertDataBackground(original: JsonNode, mapper: ObjectMapper): JsonNode
    fun convertModelMath(original: JsonNode, mapper: ObjectMapper): JsonNode
}

// Utility functions
fun JsonNode.copyStringChild(originalKey: String, newNode: ObjectNode, newKey: String) {
    this[originalKey]?.let { newNode.put(newKey, it.textValue()) }
}

fun JsonNode.copyBooleanChild(originalKey: String, newNode: ObjectNode, newKey: String) {
    this[originalKey]?.let { newNode.put(newKey, it.asText()) }
}

fun JsonNode.copyDateChild(originalKey: String, newNode: ObjectNode, newKey: String) {
    if (has(originalKey)) {
        val originalDate = this[originalKey] as ArrayNode
        val localDate = LocalDate.of(originalDate[0].intValue(), originalDate[1].intValue(), originalDate[2].intValue())
        newNode.put(newKey, localDate.toString())
    }
}

fun convertContact(originalNode: JsonNode, mapper: ObjectMapper): JsonNode {

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

fun convertReference(originalNode: JsonNode, mapper: ObjectMapper): JsonNode {

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

fun convertProduct(originalNode: JsonNode, mapper: ObjectMapper): JsonNode {

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

fun convertHazard(originalNode: JsonNode, mapper: ObjectMapper): JsonNode {

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

fun convertStudy(originalNode: JsonNode, mapper: ObjectMapper): JsonNode {

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

fun convertStudySample(originalNode: JsonNode, mapper: ObjectMapper): JsonNode {
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

fun convertLaboratory(originalNode: JsonNode, mapper: ObjectMapper): JsonNode {

    val newNode = mapper.createObjectNode()
    // FIXME: string arrays are not working with MEDIFIT API
//    newNode.set<ObjectNode>("fsk:accreditation", originalNode["accreditation"])
    originalNode.copyStringChild("name", newNode, "fsk:name")
    originalNode.copyStringChild("country", newNode, "fsk:country")

    return newNode
}

fun convertAssay(originalNode: JsonNode, mapper: ObjectMapper): JsonNode {

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

fun convertParameter(originalNode: JsonNode, mapper: ObjectMapper): JsonNode {

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
