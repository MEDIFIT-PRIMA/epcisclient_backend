package com.github.medifitprima.epcisclient.metadata

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.node.ObjectNode

class PredictiveModelConverter : MetadataConverter {

    override fun convertGeneralInformation(original: JsonNode, mapper: ObjectMapper): JsonNode {
        val newNode = mapper.createObjectNode()

        original.copyStringChild("name", newNode, "fsk:name")
        original.copyStringChild("source", newNode, "fsk:source")
        original.copyStringChild("identifier", newNode, "fsk:identifier")

        if (original.has("author")) {
            val newAuthors = newNode.putArray("fsk:author")
            original["author"].map { convertContact(it, mapper) }.forEach(newAuthors::add)
        }

        if (original.has("creator")) {
            val newCreators = newNode.putArray("fsk:creator")
            original["creator"].map { convertContact(it, mapper) }.forEach(newCreators::add)
        }

        // TODO: creationDate (LocalDate)
        // TODO: modificationDate (LocalDate[])

        original.copyStringChild("rights", newNode, "fsk:rights")
        original.copyStringChild("availability", newNode, "fsk:availability")
        original.copyStringChild("url", newNode, "fsk:url")
        original.copyStringChild("format", newNode, "fsk:format")

        if (original.has("reference")) {
            val newReferences = newNode.putArray("fsk:reference")
            original["reference"].map { convertContact(it, mapper) }
                .forEach(newReferences::add)
        }

        original.copyStringChild("language", newNode, "fsk:language")
        original.copyStringChild("software", newNode, "fsk:software")
        original.copyStringChild("languageWrittenIn", newNode, "fsk:languageWrittenIn")

        // TODO: modelCategory

        original.copyStringChild("status", newNode, "fsk:status")
        original.copyStringChild("objective", newNode, "fsk:objective")
        original.copyStringChild("description", newNode, "fsk:description")

        return newNode
    }

    override fun convertScope(original: JsonNode, mapper: ObjectMapper): JsonNode {
        val newNode = mapper.createObjectNode()

        if (original.has("product")) {
            val newProducts = newNode.putArray("fsk:product")
            original["product"].map { convertProduct(it, mapper) }.forEach(newProducts::add)
        }

        if (original.has("hazard")) {
            val newHazards = newNode.putArray("fsk:hazard")
            original["hazard"].map { convertHazard(it, mapper) }.forEach(newHazards::add)
        }

        original.copyStringChild("generalComment", newNode, "fsk:generalComment")
        original.copyStringChild("temporalInformation", newNode, "fsk:temporalInformation")

        // TODO: spatial information

        return newNode
    }

    override fun convertDataBackground(original: JsonNode, mapper: ObjectMapper): JsonNode {
        val newNode = mapper.createObjectNode()

        newNode.set<ObjectNode>("fsk:study", convertStudy(original["study"], mapper))

        if (original.has("studySample")) {
            val newSamples = newNode.putArray("fsk:studySample")
            original["studySample"].map { convertStudySample(it, mapper) }.forEach(newSamples::add)
        }

        if (original.has("laboratory")) {
            val newLabs = newNode.putArray("fsk:laboratory")
            original["laboratory"].map { convertLaboratory(it, mapper) }.forEach(newLabs::add)
        }

        if (original.has("assay")) {
            val newAssays = newNode.putArray("fsk:assay")
            original["assay"].map { convertAssay(it, mapper) }.forEach(newAssays::add)
        }

        return newNode
    }

    override fun convertModelMath(original: JsonNode, mapper: ObjectMapper): JsonNode {
        val newNode = mapper.createObjectNode()

        if (original.has("parameter")) {
            val newParameters = newNode.putArray("fsk:parameter")
            original["parameter"].map { convertParameter(it, mapper) }.forEach(newParameters::add)
        }

        if (original.has("qualityMeasures")) {
            val newMeasures = newNode.putArray("fsk:qualityMeasures")
            original["qualityMeasures"].map { convertQualityMeasures(it, mapper) }.forEach(newMeasures::add)
        }

        if (original.has("modelEquation")) {
            val newEquations = newNode.putArray("modelEquation")
            original["qualityMeasures"].map { convertModelEquation(it, mapper) }.forEach(newEquations::add)
        }

        original.copyStringChild("fittingProcedure", newNode, "fsk:fittingProcedure")

        if (original.has("event")) {
            newNode.set<ObjectNode>("event", original["event"])
        }

        return newNode
    }
}