package com.github.medifitprima.epcisclient

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.node.ArrayNode
import com.fasterxml.jackson.databind.node.ObjectNode
import com.fasterxml.jackson.databind.node.TextNode
import com.fasterxml.jackson.databind.node.ValueNode
import java.text.SimpleDateFormat

import java.util.*
import java.util.Locale

/**
 *  Function to add the prefix "fskx:" before each metadata property. That is going to be the body of the EPCIS event.
 */
fun prepareEpcisBody(

    jsonNode: JsonNode,
    mapper: ObjectMapper
):JsonNode {
    val jsonBody : JsonNode

    val prefix = "fskx:"
    if (jsonNode.isObject) {
        val objectNodeNew = mapper.createObjectNode()
        val objectNode = jsonNode as ObjectNode
        val iter = objectNode.fields()

        while (iter.hasNext()) {
            val entry = iter.next()
            // filter empty fields to avoid schema conflicts
            if(entry.value.toString() != "null" && entry.value.toString() != "[]" && entry.value.textValue() != "") {
                // special case if modelType starts with uppercase (e.g.: "GenericModel")
                if(entry.key.toString().lowercase() == "modeltype"){
                    val lowerCaseModelType = entry.value.textValue()
                        .replaceFirstChar { it.lowercase(Locale.getDefault()) }
                    objectNodeNew.set<ObjectNode>(prefix + entry.key, prepareEpcisBody(TextNode(lowerCaseModelType), mapper))
                } else {
                    objectNodeNew.set<ObjectNode>(prefix + entry.key, prepareEpcisBody(entry.value, mapper))
                }
            }
        }
        jsonBody = objectNodeNew
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
fun createEpcisAddEvent(medifitMetadata: ObjectNode,
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
    //downloadURL:
    val bisTransactionObject = """{
                        "type": "testprd",
                        "bizTransaction": "fskx:$downloadUrl"
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
                    "https://ref.gs1.org/standards/epcis/epcis-context.jsonld",
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
    //val df: DateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSX", Locale.getDefault())
    //val lt: LocalDateTime = LocalDateTime.now()
    //var formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSX")
    val now = Date()

    val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ", Locale.getDefault())
    return simpleDateFormat.format(now).toString().dropLast(2)+ ":00"
    //return lt.format(formatter).toString()
    //return LocalDateTime.now().toString() + "Z"
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




fun createExecutionEventBody(model_id: String,
                             model_name: String,
                             mapper: ObjectMapper): ObjectNode {
    val body = """{
        "@context": [
            "https://ref.gs1.org/standards/epcis/epcis-context.jsonld",
            {
                "fskx": "https://medifit-prima.github.io/fsklab-json/1.0.4/FSKModel.json"
            }
        ],
        "id": "fskx:test:document5",
        "type": "EPCISDocument",
        "schemaVersion": "2.0",
        "creationDate": "${getEventTime()}",
        "epcisBody": {
            "eventList": [
                {
                    "fskx:modelType": "genericModel",
                    "fskx:generalInformation": {
                        "fskx:identifier": "$model_id",
                        "fskx:name": "$model_name"
                    },
                    "type": "ObjectEvent",
                    "action": "OBSERVE",
                    "bizStep": "commissioning",
                    "disposition": "active",
                    "epcList": [
                        "fskx:model:$model_id"
                    ],
                    "eventTime": "${getEventTime()}",
                    "eventTimeZoneOffset": "${getTimeZoneOffset()}",
                    "readPoint": {
                        "id":  "fskx:microhibro"
                    }
                    
                }
            ]
        }
    }""".trimIndent()

    val event = mapper.readTree(body) as ObjectNode//mapper.createObjectNode()
    return event
}
