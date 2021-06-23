package com.github.medifitprima.epcisclient

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.threetenbp.ThreeTenModule
import com.fasterxml.jackson.module.kotlin.KotlinModule
import org.junit.Test
import java.io.File
import kotlin.test.assertTrue

class MetadataTest {

    companion object {
        val mapper: ObjectMapper = ObjectMapper().registerModule(KotlinModule()).registerModule(ThreeTenModule())
    }

    @Test
    fun testConvertGenericModel() {
        val tree = mapper.readTree(File("src/test/resources/genericModel.json"))
        val medifitMetadata = createMedifitMetadata(tree, mapper)
        medifitMetadata.checkMetadata()
    }

    @Test
    fun testConvertPredictiveModel() {
        val tree = mapper.readTree(File("src/test/resources/predictiveModel.json"))
        val medifitMetadata = createMedifitMetadata(tree, mapper)
        medifitMetadata.checkMetadata()
    }

    @Test
    fun testConvertDataModel() {
        val tree = mapper.readTree(File("src/test/resources/dataModel.json"))
        val medifitMetadata = createMedifitMetadata(tree, mapper)
        medifitMetadata.checkMetadata()
    }

    @Test
    fun testConvertOtherModel() {
        val tree = mapper.readTree(File("src/test/resources/otherModel.json"))
        val medifitMetadata = createMedifitMetadata(tree, mapper)
        medifitMetadata.checkMetadata()
    }

    @Test
    fun testConvertExposureModel() {
        val tree = mapper.readTree(File("src/test/resources/exposureModel.json"))
        val medifitMetadata = createMedifitMetadata(tree, mapper)
        medifitMetadata.checkMetadata()
    }

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
