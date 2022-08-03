package com.github.medifitprima.epcisclient

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.node.ArrayNode
import com.fasterxml.jackson.databind.node.ObjectNode
import com.fasterxml.jackson.databind.node.ValueNode
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.util.*


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
fun createMedifitMetadataNew(medifitMetadata: ObjectNode,
                             mapper: ObjectMapper,
                             downloadUrl:String): ObjectNode{
    medifitMetadata.put("type","ObjectEvent")
    medifitMetadata.put("eventID","urn:uuid:" + UUID.randomUUID())
    //medifitMetadata.put("eventTimeZoneOffset","+02:00")
    medifitMetadata.put("eventTimeZoneOffset", getTimeZoneOffset())
    //medifitMetadata.put("eventTime","2022-06-16T08:14:16Z")
    medifitMetadata.put("eventTime", getEventTime())
    medifitMetadata.put("action","ADD")
    medifitMetadata.put("bizStep","commissioning")
    medifitMetadata.put("disposition","completeness_inferred")
    val bisTransactionListJson = mapper.createArrayNode()
    val bisTransactionObject = """{
                        "type": "po",
                        "bizTransaction": "$downloadUrl"
                    }""".trimIndent() // //"urn:epcglobal:cbv:btt:testprd",
    bisTransactionListJson.add( mapper.readTree(bisTransactionObject) as ObjectNode)

        val readPointObject = """{
                        "id": "fskx:microhibro"
                    }""".trimIndent()
    //bisTransactionListJson.add( mapper.readTree(bisTransactionObject) as ObjectNode)
    medifitMetadata.set<ObjectNode>("readPoint", mapper.readTree(readPointObject) as ObjectNode)
    medifitMetadata.set<ArrayNode>("bizTransactionList", bisTransactionListJson)

    val epcList = mapper.createArrayNode()
    epcList.add("fskx:model:" + getModelIdentifier(medifitMetadata))//UUID.randomUUID())
    medifitMetadata.set<ArrayNode>("epcList", epcList)

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
                "creationDate": "${getEventTime()}",
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
fun getEventTime():String{
    //val date = -(Calendar.get(Calendar.ZONE_OFFSET) + Calendar.get(Calendar.DST_OFFSET)) / (60 * 1000)

    return LocalDateTime.now().toString() + "Z"
}
fun getTimeZoneOffset():String{

    val calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT"), Locale.getDefault())
    val timeZone = SimpleDateFormat("Z").format(calendar.time)
    return timeZone.substring(0, 3) + ":" + timeZone.substring(3, 5)

}
fun getModelIdentifier(medifitMetadata: ObjectNode):String{
    val identifier = medifitMetadata.get("fskx:generalInformation").get("fskx:identifier")
    return identifier.textValue()
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
