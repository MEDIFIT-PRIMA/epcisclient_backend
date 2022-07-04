package com.github.medifitprima.epcisclient

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.node.ArrayNode
import com.fasterxml.jackson.databind.node.ObjectNode
import com.fasterxml.jackson.databind.node.ValueNode
import java.time.LocalDate


fun prepareEpcisBody(

    jsonNode: JsonNode,
    mapper: ObjectMapper
):JsonNode {
    var jsonBody : JsonNode

    var prefix = "fskx:"
    if (jsonNode.isObject) {
        val objectNode_new = mapper.createObjectNode()
        val objectNode = jsonNode as ObjectNode
        val iter = objectNode.fields()

        while (iter.hasNext()) {
            val entry = iter.next()
            objectNode_new.set<ObjectNode>(prefix + entry.key, prepareEpcisBody(entry.value, mapper))
        }
        jsonBody = objectNode_new
    } else {
        if(jsonNode.isArray){
            val arrayNode = jsonNode as ArrayNode
            val arrayNode_new = mapper.createArrayNode()
            for (i in 0 until arrayNode.size()) {
                if(arrayNode[i].toString() != "null"){
                    arrayNode_new.add(prepareEpcisBody(arrayNode[i],mapper))
                }

            }
            jsonBody = arrayNode_new
        } else {

            jsonBody = jsonNode as ValueNode
        }
    }
   return jsonBody
}
fun createMedifitMetadataNew(medifitMetadata: ObjectNode, mapper: ObjectMapper): ObjectNode{
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
    return event
}


/** Utility data class for the API. Instead of sending the complete original metadata only keeps a subset used in the
 * frontend. */
data class ModelView(
    val type: String,
    val name: String,
    val software: String,
    val products: List<String>,
    val hazards: List<String>
)

fun extractModelView(model: JsonNode): ModelView {
    val modelType = model["fsk:modelType"]?.textValue() ?: ""
    var name = ""
    var software = ""
    val products = mutableListOf<String>()
    val hazards = mutableListOf<String>()

    if (model.has("fsk:generalInformation")) {
        val generalInformation = model["fsk:generalInformation"]
        name = generalInformation["fsk:name"]?.textValue() ?: ""
        software = generalInformation["fsk:languageWrittenIn"]?.textValue() ?: ""
    }

    if (model.has("fsk:scope")) {
        val scope = model["fsk:scope"]

        // Get products{
        scope["fsk:product"].map { productNode ->
            if (productNode.has("fsk:name")) {
                products.add(productNode["fsk:name"].textValue())
            }
        }

        // Get hazards
        scope["fsk:hazard"].map { hazardNode ->
            if (hazardNode.has("fsk:name")) {
                hazards.add(hazardNode["fsk:name"].textValue())
            }
        }
    }

    return ModelView(modelType, name, software, products, hazards)
}
