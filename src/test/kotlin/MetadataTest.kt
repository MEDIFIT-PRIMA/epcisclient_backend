package com.github.medifitprima.epcisclient

import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.Test
import java.io.File
import kotlin.test.assertNotNull

class MetadataTest {

    companion object {
        val mapper = ObjectMapper()
    }

    @Test
    fun testConvertGenericModel() {
        val tree = mapper.readTree(File("src/test/resources/genericModel.json"))
        val medifitMetadata = createMedifitMetadata(tree, mapper)
        assertNotNull(medifitMetadata)
    }

    // TODO: testConvertDataModel
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