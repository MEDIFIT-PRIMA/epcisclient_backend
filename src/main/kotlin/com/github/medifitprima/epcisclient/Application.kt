package com.github.medifitprima.epcisclient

//import java.io.File

//import java.io.File
//import java.io.File.createTempFile
//import java.nio.file.Files.createTempDirectory
//import java.nio.file.Files.createTempDirectory
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
import java.net.URL
import java.nio.file.Files
import java.nio.file.StandardCopyOption
import java.util.*
import kotlin.io.path.*


val objectMapper = ObjectMapper()
val medifit_token = ""
val medifitClient = MedifitClient(medifit_token)

val bodyTemplate = objectMapper.readTree({}.javaClass.getResource("/bodyTemplate.json"))

//val tempFolder = java.nio.file.Files.createTempDirectory("uploads")

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
        header("api-key")
        header("api-key-secret")
        anyHost()
    }




    routing {


        get("/hello") {

            call.respond("hello world")
        }



        /*
         * Upload metadata from passed model file.
         * Body parameter: form data.
         * - file: Binary object
         */
//        post("/uploadFile") {
//            val multipartData = call.receiveMultipart()
//
//            var file: File? = null
//            try {
//                multipartData.forEachPart { part ->
//                    // if part is a file (could be form item)
//                    if (part is PartData.FileItem) {
//
//                        // retrieve file name of upload
//                        val name = part.originalFileName!!
//
//                        val fileCopy = createTempFile(name).toFile()
//                        fileCopy.deleteOnExit()
//
//                        val fileBytes = part.streamProvider().readBytes()
//                        fileCopy.writeBytes(fileBytes)
//
//                        file = fileCopy
//                    }
//                    // make sure to dispose of the part after use to prevent leaks
//                    part.dispose()
//                }
//
//                if (file != null) {
//                    val metadata = readMetadata(file!!, objectMapper)
//                    //val medifitMetadata = createMedifitMetadata(metadata, objectMapper)
//                    val medifitMetadata = prepareEpcisBody(metadata, objectMapper)
//                    //medifitMetadata.set<ObjectNode>()
//                    val event = createMedifitMetadataNew(medifitMetadata as ObjectNode, objectMapper,"https://google.com")
//
//                    //val event = bodyTemplate.get("epcisBody").get("eventList").get(0) as ObjectNode
//                    //event.set<ObjectNode>("fsk:model", medifitMetadata)
//
//                    println(bodyTemplate.toPrettyString())
//
//                    medifitClient.captureEvent(event)
//
//
//                    //call.respond(medifitMetadata)
//                    call.respond(event.toPrettyString())
//                }
//            } finally {
//                file?.delete()
//            }
//        }

        post("/registerModelURL") {
            val mUrl = call.receive<UrlJsonObject>()
            var file: File? = null
                try {
                    val url: URL = URL(mUrl.url)
                    // retrieve file name of upload
                    val name = "model.fskx"
                    val fileCopy = createTempFile(name)
                    url.openStream().use { Files.copy(it, fileCopy,StandardCopyOption.REPLACE_EXISTING) }
                    file = fileCopy.toFile()
                    if (file != null) {
                        val metadata = readMetadata(file!!, objectMapper)
                        //val medifitMetadata = createMedifitMetadata(metadata, objectMapper)
                        val medifitMetadata = prepareEpcisBody(metadata, objectMapper)
                        //medifitMetadata.set<ObjectNode>()
                        val event = createMedifitMetadataNew(medifitMetadata as ObjectNode, objectMapper, url.toString())

                        //val event = bodyTemplate.get("epcisBody").get("eventList").get(0) as ObjectNode
                        //event.set<ObjectNode>("fsk:model", medifitMetadata)

                        println(bodyTemplate.toPrettyString())

                        val capture_id = medifitClient.captureEvent(event)?:"Error"


                        //call.respond(medifitMetadata)
                        // all.respond(capture_id)
                        call.response.header("Location", capture_id)
                        call.respond(event)
                    }
                } finally {
                    file?.delete()
                }

        }

        post("/captureExecutionEvent") {
            val executionEventParameters = call.receive<ExecutionEvent>()

            try {
                val model_id: String = executionEventParameters.model_id
                val model_name: String = executionEventParameters.model_name
                val event = createExecutionEventBody(model_id, model_name, objectMapper)
                val capture_id = medifitClient.captureEvent(event)?:"Error"



                call.respond(capture_id)
                //call.respond(event)

            } catch (exception: Exception){
                call.respond(exception)
            }

        }
    }


}

data class UrlJsonObject(val url: String)
data class ExecutionEvent(val model_id: String, val model_name: String)

class MedifitClient(private val token: String) {

    private val url = "https://epcis.medifit-prima.net/"
    val appConfiguration :Properties = loadConfiguration()
    private val api_key = appConfiguration.getProperty("api-key")?:""
    private val api_key_secret = appConfiguration.getProperty("api-key-secret")?:""
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
                models = mutableListOf()
                val jsonResponse = objectMapper.readTree(response.readText())
                val eventList2 = jsonResponse.get("eventList")
                eventList2.forEach { models.add(it.get("fsk:model")) }
            } else {
                models = mutableListOf()
            }
        }

        models
    }

    suspend fun getSimpleModels(): List<ModelView> {

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
                val jsonResponse: JsonNode = withContext(Dispatchers.Default) {
                    objectMapper.readTree(response.readText())
                }
                val eventList = jsonResponse.get("eventList")

                return eventList.map { it["fsk:model"] }.map { extractModelView(it) }
            }
        }

        return emptyList()
    }

    suspend fun captureEvent(bodyParameter: JsonNode): String? {
        HttpClient().use { client ->
            val response: HttpResponse = client.post("$url/capture") {
                headers {
                    //append(HttpHeaders.Authorization, token)
                    append("API-KEY",api_key)
                    append("API-KEY-SECRET",api_key_secret)
                }
                contentType(ContentType.Application.Json)
                body = bodyParameter.toPrettyString()
            }
            val location = response.headers.get("Location")
            println("Upload response: " + response.status)
            return location
        }
    }
}
fun loadConfiguration(): Properties {

    val properties = Properties()

    val configFileInUserFolder = File(System.getProperty("user.home"), "epcis_backend.properties")

    if (configFileInUserFolder.exists()) {
        configFileInUserFolder.inputStream().use {
            properties.load(it)
        }
    } else {
        val catalinaFolder = System.getProperty("catalina.home")
        if (catalinaFolder != null && File(catalinaFolder, "epcis_backend.properties").exists()) {
            File(catalinaFolder, "epcis_backend.properties").inputStream().use {
                properties.load(it)
            }
        } else {
            error("Configuration file not found")
        }
    }

    return properties
}
fun main(args: Array<String>): Unit = io.ktor.server.tomcat.EngineMain.main(args)
