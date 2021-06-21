import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.node.ObjectNode
import de.bund.bfr.fskml.FSKML
import de.unirostock.sems.cbarchive.ArchiveEntry
import de.unirostock.sems.cbarchive.CombineArchive
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
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import kotlin.io.path.createTempDirectory

val medifit_token = "" // TODO: replace with config file

suspend fun main(args: Array<String>) {

    val client = HttpClient()
    val objectMapper = ObjectMapper()

    val bodyTemplate = objectMapper.readTree({}.javaClass.getResource("/bodyTemplate.json"))

    embeddedServer(Netty, port = 8000) {

        val tempFolder = createTempDirectory("uploads")

        install(ContentNegotiation) {
            jackson()
        }

        install(CORS) {
            method(HttpMethod.Get)
            method(HttpMethod.Post)
            header(HttpHeaders.AccessControlAllowHeaders)
            header(HttpHeaders.ContentType)
            header(HttpHeaders.AccessControlAllowOrigin)
            allowCredentials = true
            anyHost()
        }

        routing {
            get("/") {
                call.respondText("Hello, world!")
            }

            get("/models") {
                val models = getModels(client, objectMapper)
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

                    uploadModel(client, bodyTemplate)

                    // TODO: return OK
                    call.respond(medifitMetadata)
                }
            }
        }
    }.start(wait = true)
}

private fun readMetadata(file: File, objectMapper: ObjectMapper): JsonNode {
    CombineArchive(file).use { archive ->
        val jsonUri = FSKML.getURIS(1, 0, 12)["json"]!!

        val jsonString = archive.getEntriesWithFormat(jsonUri)
            .first { it.fileName == "metaData.json" }
            .loadTextEntry()

        return objectMapper.readTree(jsonString)
    }
}

private suspend fun getModels(client: HttpClient, objectMapper: ObjectMapper): List<JsonNode> {
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

    val models = mutableListOf<JsonNode>()

    if (response.status == HttpStatusCode.OK) {
        withContext(Dispatchers.IO) {
            val jsonResponse = objectMapper.readTree(response.readText())
            val eventList = jsonResponse.get("eventList")
            eventList.forEach { models.add(it.get("fsk:model")) }
        }
    }

    return models
}

private suspend fun uploadModel(client: HttpClient, bodyParameter: JsonNode) {
    val url = "https://demo-repository.openepcis.io"
    val response: HttpResponse = client.post("$url/capture") {
        headers {
            append(HttpHeaders.Authorization, medifit_token)
        }
        contentType(ContentType.Application.Json)
        body = bodyParameter.toPrettyString()
    }

    // TODO: process response code
    println("/capture response code: ${response.status}")
}

private fun ArchiveEntry.loadTextEntry(): String {
    val tempFile = createTempFile()
    return try {
        extractFile(tempFile)
        tempFile.readText()
    } finally {
        tempFile.delete()
    }
}
