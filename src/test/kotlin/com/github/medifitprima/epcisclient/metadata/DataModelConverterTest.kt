package com.github.medifitprima.epcisclient.com.github.medifitprima.epcisclient.metadata

import com.fasterxml.jackson.databind.ObjectMapper
import com.github.medifitprima.epcisclient.metadata.DataModelConverter
import com.github.medifitprima.epcisclient.metadata.GenericModelConverter
import org.junit.Test
import java.io.File
import kotlin.test.assertNotNull

class DataModelConverterTest {

    companion object {
        val mapper = ObjectMapper()
        val tree = mapper.readTree(File("src/test/resources/dataModel.json"))
    }

    @Test
    fun testGeneralInformation() {
        val converter = DataModelConverter()
        val convertedNode = converter.convertGeneralInformation(tree["generalInformation"], mapper)
        assertNotNull(convertedNode)
    }

    @Test
    fun testScope() {
        val converter = DataModelConverter()
        val convertedNode = converter.convertScope(tree["scope"], mapper)
        assertNotNull(convertedNode)
    }

    @Test
    fun testDataBackground() {
        val converter = DataModelConverter()
        val convertedNode = converter.convertDataBackground(tree["dataBackground"], mapper)
        assertNotNull(convertedNode)
    }

    @Test
    fun testModelMath() {
        val converter = DataModelConverter()
        val convertedNode = converter.convertModelMath(tree["modelMath"], mapper)
        assertNotNull(convertedNode)
    }
}
