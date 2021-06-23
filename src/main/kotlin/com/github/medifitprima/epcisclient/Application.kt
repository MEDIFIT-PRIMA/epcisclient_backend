package com.github.medifitprima.epcisclient

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.node.ObjectNode
import io.ktor.application.*
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.features.*
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.jackson.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import kotlin.io.path.createTempDirectory

val objectMapper = ObjectMapper()
val medifit_token = ""
val medifitClient = MedifitClient(medifit_token)

val bodyTemplate = objectMapper.readTree({}.javaClass.getResource("/bodyTemplate.json"))

val tempFolder = createTempDirectory("uploads")

fun Application.module(testing: Boolean = false) {

    install(CallLogging)
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
            val models = medifitClient.getModels()
            call.respond(models)
        }

        /*
         * Upload metadata from passed model file.
         * Body parameter: form data.
         * - file: Binary object
         */
        post("/upload") {
            val multipartData = call.receiveMultipart()

            var file: File? = null
            multipartData.forEachPart { part ->
                // if part is a file (could be form item)
                if (part is PartData.FileItem) {
                    // retrieve file name of upload
                    val name = part.originalFileName!!

                    val fileCopy = kotlin.io.path.createTempFile(tempFolder, name).toFile()
                    fileCopy.deleteOnExit()

                    val fileBytes = part.streamProvider().readBytes()
                    fileCopy.writeBytes(fileBytes)

                    file = fileCopy
                }
                // make sure to dispose of the part after use to prevent leaks
                part.dispose()
            }

            if (file != null) {
                val metadata = readMetadata(file!!, objectMapper)
                val medifitMetadata = createMedifitMetadata(metadata, objectMapper)

                // TODO: inject medifitMetadata into bodyTemplate and upload to MEDIFIT API
                val event = bodyTemplate.get("epcisBody").get("eventList").get(0) as ObjectNode
                event.set<ObjectNode>("fsk:model", medifitMetadata)

                println(bodyTemplate.toPrettyString())

                medifitClient.uploadModel(bodyTemplate)

                // TODO: return OK
                call.respond(medifitMetadata)
            }
        }
    }
}

class MedifitClient(private val token: String) {

    private val url = "https://demo-repository.openepcis.io"

    suspend fun getModels(): List<JsonNode> = withContext(Dispatchers.IO) {

        val models: MutableList<JsonNode>

        HttpClient().use { client ->
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

            if (response.status == HttpStatusCode.OK) {
                models = mutableListOf<JsonNode>()
                val jsonResponse = objectMapper.readTree(response.readText())
                val eventList2 = jsonResponse.get("eventList")
                eventList2.forEach { models.add(it.get("fsk:model")) }
            } else {
                models = mutableListOf()
            }
        }

        models
    }

    suspend fun uploadModel(bodyParameter: JsonNode) {
        HttpClient().use { client ->
            val response: HttpResponse = client.post("$url/capture") {
                headers {
                    append(HttpHeaders.Authorization, token)
                }
                contentType(ContentType.Application.Json)
                body = bodyParameter.toPrettyString()
            }
            println("Upload response: " + response.status)
        }
    }
}

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)
