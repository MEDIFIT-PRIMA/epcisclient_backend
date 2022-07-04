package com.github.medifitprima.epcisclient

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.node.ArrayNode
import com.fasterxml.jackson.databind.node.ObjectNode
import com.fasterxml.jackson.databind.node.ValueNode
import com.fasterxml.jackson.datatype.threetenbp.ThreeTenModule
import com.fasterxml.jackson.module.kotlin.KotlinModule
import org.json.simple.JSONObject
import org.junit.Test
import java.io.File
import kotlin.reflect.jvm.internal.impl.load.kotlin.JvmType
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class MetadataTest {

    companion object {
        val mapper: ObjectMapper = ObjectMapper().registerModule(KotlinModule()).registerModule(ThreeTenModule())
    }


    @Test
    fun testAddKeys() {
        val tree = mapper.readTree(File("src/test/resources/model_test2.json"))
        var medifitMetadata= prepareEpcisBody(tree,mapper) as ObjectNode

        medifitMetadata.put("type","ObjectEvent")
        medifitMetadata.put("eventID","urn:uuid:5d3c82cc-3fb2-4b70-af4a-914f1f839c5b")
        medifitMetadata.put("eventTimeZoneOffset","+02:00")
        medifitMetadata.put("eventTime","2022-06-16T08:14:16Z")
        medifitMetadata.put("action","ADD")
        medifitMetadata.put("bizStep","commissioning")
        medifitMetadata.put("disposition","completeness_inferred")
        val bisTransactionListJson = mapper.createArrayNode()
        val bisTransactionObject = """{
                        "type": "urn:epcglobal:cbv:btt:po",
                        "bizTransaction": "http://transaction.acme.com/po/12345679"
                    }""".trimIndent()
        bisTransactionListJson.add( mapper.readTree(bisTransactionObject) as ObjectNode)

//        val readPointObject = """{
//                        "id": "urn:epc:id:sgln:0123456.78912.44"
//                    }""".trimIndent()
        bisTransactionListJson.add( mapper.readTree(bisTransactionObject) as ObjectNode)
        //medifitMetadata.set<ObjectNode>("readPoint", mapper.readTree(readPointObject) as ObjectNode)
        medifitMetadata.set<ArrayNode>("bizTransactionList", bisTransactionListJson)

            val body = """{
                "@context": [
                    "https://gs1.github.io/EPCIS/epcis-context.jsonld",
                    {
                        "fskx": "https://medifit-prima.github.io/fsklab-json/1.0.4/FSKModel.json"
                    }
                ],
                "id": "fskx:test:document5",
                "type": "EPCISDocument",
                "schemaVersion": "2.0",
                "creationDate": "2021-03-03T11:30:47.0Z",
                "epcisBody": {
                    "eventList": []
                    }
                 }""".trimIndent()
        val event = mapper.readTree(body) as ObjectNode//mapper.createObjectNode()
        val eventList = event.get("epcisBody").get("eventList") as ArrayNode
        eventList.add(medifitMetadata)
        event.toPrettyString()
    }

}

fun JsonNode.checkMetadata() {
    assertTrue(has("fsk:modelType"))
    assertTrue(has("fsk:generalInformation"))
    assertTrue(has("fsk:scope"))
    assertTrue(has("fsk:dataBackground"))
    assertTrue(has("fsk:modelMath"))
}
