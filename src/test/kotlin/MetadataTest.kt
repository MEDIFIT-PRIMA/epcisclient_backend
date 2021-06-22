package com.github.medifitprima.epcisclient

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.Test
import java.io.File
import kotlin.test.assertTrue

class MetadataTest {

    companion object {
        val mapper = ObjectMapper()
    }

    @Test
    fun testConvertGenericModel() {
        val tree = mapper.readTree(File("src/test/resources/genericModel.json"))
        val medifitMetadata = createMedifitMetadata(tree, mapper)
        medifitMetadata.checkMetadata()
    }

    @Test
    fun testConvertDataModel() {
        val tree = mapper.readTree(File("src/test/resources/dataModel.json"))
        val medifitMetadata = createMedifitMetadata(tree, mapper)
        medifitMetadata.checkMetadata()
    }

    // TODO: testConvertPredictiveModel
    // TODO: testConvertOtherModel
    // TODO: testConvertExposureModel
    // TODO: testConvertToxicologicalModel
    // TODO: testConvertDoseResponseModel
    // TODO: testConvertProcessModel
    // TODO: testConvertConsumptionModel
    // TODO: testConvertHealthModel
    // TODO: testConvertRiskModel
    // TODO: testConvertQraModel
}

fun JsonNode.checkMetadata() {
    assertTrue(has("fsk:modelType"))
    assertTrue(has("fsk:generalInformation"))
    assertTrue(has("fsk:scope"))
    assertTrue(has("fsk:dataBackground"))
    assertTrue(has("fsk:modelMath"))
}
