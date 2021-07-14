package com.github.medifitprima.epcisclient.metadata

import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.Test
import java.io.File
import kotlin.test.assertNotNull

class ExposureModelConverterTest {

    companion object {
        val mapper = ObjectMapper()
        val tree = mapper.readTree(File("src/test/resources/exposureModel.json"))
    }

    @Test
    fun testGeneralInformation() {
        val converter = ExposureModelConverter()
        val convertedNode = converter.convertGeneralInformation(tree["generalInformation"], mapper)
        assertNotNull(convertedNode)
    }

    @Test
    fun testScope() {
        val converter = ExposureModelConverter()
        val convertedNode = converter.convertScope(tree["scope"], mapper)
        assertNotNull(convertedNode)
    }

    @Test
    fun testDataBackground() {
        val converter = ExposureModelConverter()
        val convertedNode = converter.convertDataBackground(tree["dataBackground"], mapper)
        assertNotNull(convertedNode)
    }

    @Test
    fun testModelMath() {
        val converter = ExposureModelConverter()
        val convertedNode = converter.convertModelMath(tree["modelMath"], mapper)
        assertNotNull(convertedNode)
    }
}
