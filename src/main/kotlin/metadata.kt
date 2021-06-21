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
//        objectMapper.writerWithDefaultPrettyPrinter().writeValue(System.out, metadata)

        val medifitMetadata = createMedifitMetadata(metadata, mapper)
        mapper.writerWithDefaultPrettyPrinter().writeValue(System.out, medifitMetadata)
    }
}

fun createMedifitMetadata(fskmlMetadata: JsonNode, objectMapper: ObjectMapper): JsonNode {

    val objectNode = objectMapper.createObjectNode()

    // fsk:modelType
    val modelType = fskmlMetadata["modelType"]
    objectNode.put("fsk:modelType", convertModelType(modelType.textValue()))

    if (modelType.textValue() == "genericModel") {
        objectNode.put("fsk:generalInformation", convertGeneralInformation(fskmlMetadata["generalInformation"], objectMapper))
        objectNode.put("fsk:scope", convertScope(fskmlMetadata["scope"], objectMapper))
        objectNode.put("fsk:dataBackground", convertDataBackground(fskmlMetadata["dataBackground"], objectMapper))
        objectNode.put("fsk:modelMath", convertModelMath(fskmlMetadata["modelMath"], objectMapper))
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
        val newAuthors = mapper.createArrayNode()
        originalNode["author"].forEach { newAuthors.add(convertContact(it, mapper)) }
        newNode.set<ObjectNode>("fsk:author", newAuthors)
    }

    if (originalNode.has("creator")) {
        val newAuthors = mapper.createArrayNode()
        originalNode["creator"].forEach { newAuthors.add(convertContact(it, mapper)) }
        newNode.set<ObjectNode>("fsk:creator", newAuthors)
    }

    // TODO: creationDate (LocalDate)
    // TODO: modificationDate (LocalDate[])

    originalNode.copyStringChild("rights", newNode, "fsk:rights")
    originalNode.copyStringChild("availability", newNode, "fsk:rights")
    originalNode.copyStringChild("url", newNode, "fsk:url")
    originalNode.copyStringChild("format", newNode, "fsk:format")

    if (originalNode.has("reference")) {
        val newArray = mapper.createArrayNode()
        originalNode["reference"].forEach { newArray.add(convertReference(it, mapper)) }
        newNode.set<ObjectNode>("fsk:reference", newArray)
    }

    originalNode.copyStringChild("language", newNode, "fsk:language")
    originalNode.copyStringChild("status", newNode, "fsk:status")
    originalNode.copyStringChild("objective", newNode, "fsk:objective")
    originalNode.copyStringChild("description", newNode, "fsk:description")

    return newNode
}

private fun convertScope(originalNode: JsonNode, mapper: ObjectMapper): JsonNode {

    val newNode = mapper.createObjectNode()

    if (originalNode.has("product")) {
        val newProducts = mapper.createArrayNode()
        originalNode["product"].forEach { newProducts.add(convertProduct(it, mapper)) }
        newNode.set<ObjectNode>("fsk:product", newProducts)
    }

    if (originalNode.has("hazard")) {
        val newHazards = mapper.createArrayNode()
        originalNode["hazard"].forEach { newHazards.add(convertHazard(it, mapper)) }
        newNode.set<ObjectNode>("fsk:hazard", newHazards)
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

private fun convertDataBackground(originalNode: JsonNode, mapper: ObjectMapper): JsonNode {

    val newNode = mapper.createObjectNode()
    newNode.set<ObjectNode>("fsk:study", convertStudy(originalNode["study"], mapper))

    if (originalNode.has("studySample")) {
        val newSamples = mapper.createArrayNode()
        originalNode["studySample"].forEach { newSamples.add(convertStudySample(it, mapper)) }
        newNode.set<ObjectNode>("fsk:studySample", newSamples)
    }

    if (originalNode.has("laboratory")) {
        val newLabs = mapper.createArrayNode()
        originalNode["laboratory"].forEach { newLabs.add(convertLaboratory(it, mapper)) }
        newNode.set<ObjectNode>("fsk:laboratory", newLabs)
    }

    if (originalNode.has("assay")) {
        val newAssays = mapper.createArrayNode()
        originalNode["assay"].map { convertAssay(it, mapper) }.forEach(newAssays::add)
        newNode.set<ObjectNode>("fsk:assay", newAssays)
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

private fun convertModelMath(originalNode: JsonNode, mapper: ObjectMapper): JsonNode {

    val newNode = mapper.createObjectNode()
    // TODO: implement convertModelMath
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

    if (originalNode.has("method")) {
        newNode.set<ObjectNode>("fsk:method", originalNode["method"])
    }

    if (originalNode.has("packaging")) {
        newNode.set<ObjectNode>("fsk:packaging", originalNode["packaging"])
    }

    if (originalNode.has("treatment")) {
        newNode.set<ObjectNode>("fsk:treatment", originalNode["treatment"])
    }

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
    newNode.set<ObjectNode>("fsk:accreditation", originalNode["accreditation"])
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

private fun JsonNode.copyStringChild(originalKey: String, newNode: ObjectNode, newKey: String) {
    this[originalKey]?.let { newNode.put(newKey, it.textValue()) }
}

private fun JsonNode.copyBooleanChild(originalKey: String, newNode: ObjectNode, newKey: String) {
    this[originalKey]?.let { newNode.put(newKey, it.booleanValue()) }
}

private fun JsonNode.copyDateChild(originalKey: String, newNode: ObjectNode, newKey: String) {
    if (has(originalKey)) {
        val originalDate = this[originalKey] as ArrayNode
        val localDate = LocalDate.of(originalDate[0].intValue(), originalDate[1].intValue(), originalDate[2].intValue())
        newNode.put(newKey, localDate.toString())
    }
}