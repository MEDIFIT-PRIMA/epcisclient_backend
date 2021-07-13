package com.github.medifitprima.epcisclient.metadata

import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.Test
import java.io.File
import kotlin.test.assertNotNull

class GenericModelConverterTest {

    companion object {
        val mapper = ObjectMapper()
        val tree = mapper.readTree(File("src/test/resources/genericModel.json"))
    }

    @Test
    fun testGeneralInformation() {
        val converter = GenericModelConverter()
        val convertedNode = converter.convertGeneralInformation(tree["generalInformation"], mapper)
        assertNotNull(convertedNode)
    }

    @Test
    fun testScope() {
        val converter = GenericModelConverter()
        val convertedNode = converter.convertScope(tree["scope"], mapper)
        assertNotNull(convertedNode)
    }

    @Test
    fun testDataBackground() {
        val converter = GenericModelConverter()
        val convertedNode = converter.convertDataBackground(tree["dataBackground"], mapper)
        assertNotNull(convertedNode)
    }

    @Test
    fun testModelMath() {
        val converter = GenericModelConverter()
        val convertedNode = converter.convertModelMath(tree["modelMath"], mapper)
        assertNotNull(convertedNode)
    }
}
