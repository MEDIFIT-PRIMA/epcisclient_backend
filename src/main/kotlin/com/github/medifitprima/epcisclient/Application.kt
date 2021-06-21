package com.github.medifitprima.epcisclient

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import io.ktor.application.*
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.features.*
import io.ktor.http.*
import io.ktor.jackson.*
import io.ktor.response.*
import io.ktor.routing.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

val objectMapper = ObjectMapper()
val client = HttpClient()

fun Application.module(testing: Boolean = false) {

    install(ContentNegotiation) {
        jackson()
    }

    install(CORS) {
        method(HttpMethod.Get)
        header(HttpHeaders.AccessControlAllowHeaders)
        header(HttpHeaders.ContentType)
        header(HttpHeaders.AccessControlAllowOrigin)
        allowCredentials = true
        anyHost()
    }

    routing {

        get("/models") {
            val models = getModels(client)
            call.respond(models)
        }
    }
}

private suspend fun getModels(client: HttpClient): List<JsonNode> = withContext(Dispatchers.IO) {
    // Actual MEDIFIT request
    val url = "https://demo-repository.openepcis.io"
    // The body parameter of /queries/SimpleEventQuery seems to be NamedQueryMetaData in the spec definition
    val response: HttpResponse = client.post("$url/queries/SimpleEventQuery") {
        contentType(ContentType.Application.Json)
        body = """
            {
                "queryType": "events",
                "query": {
                    "@context": {
                        "fsk": "https://foodrisklabs.bfr.bund.de/fsk-lab-schema.json"
                    },
                    "EQ_INNER_fsk:modelType": [
                        "GenericModel",
                        "DataModel",
                        "PredictiveModel",
                        "OtherModel",
                        "ExposureModel",
                        "ToxicologicalModel",
                        "DoseResponseModel",
                        "ProcessModel",
                        "ConsumptionModel",
                        "HealthModel",
                        "RiskModel",
                        "QraModel"
                    ]
                }
            }
        """.trimIndent()
    }

    var models: MutableList<JsonNode>
    if (response.status == HttpStatusCode.OK) {
        models = mutableListOf<JsonNode>()
        val jsonResponse = objectMapper.readTree(response.readText())
        val eventList2 = jsonResponse.get("eventList")
        eventList2.forEach { models.add(it.get("fsk:model")) }
    } else {
        models = mutableListOf<JsonNode>()
    }

    models
}

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)
