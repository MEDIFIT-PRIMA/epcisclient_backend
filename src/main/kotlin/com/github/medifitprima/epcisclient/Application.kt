package com.github.medifitprima.epcisclient

//import java.io.File

//import java.io.File
//import java.io.File.createTempFile
//import java.nio.file.Files.createTempDirectory
//import java.nio.file.Files.createTempDirectory

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.node.ObjectNode
import de.unirostock.sems.cbarchive.Utils.BUFFER_SIZE
import io.ktor.application.*
import io.ktor.client.*
import io.ktor.client.engine.apache.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.features.*
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.jackson.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.util.*
import org.apache.http.HttpHost
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.net.*
import java.util.*
import kotlin.io.path.*


val objectMapper = ObjectMapper()
val medifit_token = ""
val medifitClient = MedifitClient(medifit_token)
var appHostname:String = "webproxy"
var appSocket:Int = 8080
val bodyTemplate = objectMapper.readTree({}.javaClass.getResource("/bodyTemplate.json"))
val subscriptionEvents:MutableList<JsonNode> = mutableListOf<JsonNode>()
//val tempFolder = java.nio.file.Files.createTempDirectory("uploads")

@InternalAPI
fun Application.module(testing: Boolean = false) {

    install(CallLogging)
    install(ContentNegotiation) {
        jackson()
    }

    install(CORS) {
        method(HttpMethod.Get)
        //method(HttpMethod.Post)
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
        get("/getProxy") {

            call.respond("$appHostname: $appSocket")

        }
        post("/setProxy") {
            val mproxy = call.receive<ProxyObject>()
            try {
                appHostname = mproxy.proxyHost
                appSocket = mproxy.proxySocket
                call.respond("proxy set to $appHostname: $appSocket")
            } catch(e:Exception){
                call.respond(e)
            }

        }
        post("/receiveSubscriptionEvent"){
            val b = call.receive<JsonNode>()
            subscriptionEvents.add(b)
            call.respond(HttpStatusCode.OK)
        }
        get("/eventUpdate"){
            call.respond(subscriptionEvents)
        }
        get("/clearEventUpdate"){
            subscriptionEvents.clear()
            call.respond(HttpStatusCode.OK)
        }

        post("/captureModelEvent") {
            val mUrl = call.receive<UrlJsonObject>()

            var file: File? = null
            try {
                val url = URL(mUrl.url)
                //val proxy:Proxy = Proxy(Proxy.Type.HTTP, InetSocketAddress("webproxy", 8080))

                val conn: URLConnection = url.openConnection()
                // opens input stream from the HTTP connection

                val inputStream: InputStream = conn.getInputStream()
                val name = "model.fskx"
                val fileCopy = createTempFile(name)

                // opens an output stream to save into file
                val outputStream = FileOutputStream(fileCopy.absolutePathString())

                var bytesRead = -1
                val buffer = ByteArray(BUFFER_SIZE)
                while (inputStream.read(buffer).also { bytesRead = it } != -1) {
                    outputStream.write(buffer, 0, bytesRead)
                }

                outputStream.close()
                inputStream.close()
                // retrieve file name of upload

                file = fileCopy.toFile()
                if (file != null) {
                    val metadata = readMetadata(file!!, objectMapper)
                    val medifitMetadata = prepareEpcisBody(metadata, objectMapper)
                    val event = createEpcisAddEvent(medifitMetadata as ObjectNode, objectMapper, url.toString())
                    val capture_id = medifitClient.captureEvent(event)?:"Error"


                    call.response.header("Location", capture_id)
                    call.respond(event)
                }
            }catch(e:Exception){
                try{
                    val url = URL(mUrl.url)
                    val proxy = Proxy(Proxy.Type.HTTP, InetSocketAddress(appHostname, appSocket))

                    val conn: URLConnection = url.openConnection(proxy)
                    // opens input stream from the HTTP connection

                    val inputStream: InputStream = conn.getInputStream()
                    val name = "model.fskx"
                    val fileCopy = createTempFile(name)

                    // opens an output stream to save into file
                    val outputStream = FileOutputStream(fileCopy.absolutePathString())

                    var bytesRead = -1
                    val buffer = ByteArray(BUFFER_SIZE)
                    while (inputStream.read(buffer).also { bytesRead = it } != -1) {
                        outputStream.write(buffer, 0, bytesRead)
                    }

                    outputStream.close()
                    inputStream.close()
                    // retrieve file name of upload

                    file = fileCopy.toFile()
                    if (file != null) {
                        val metadata = readMetadata(file!!, objectMapper)
                        val medifitMetadata = prepareEpcisBody(metadata, objectMapper)
                        val event = createEpcisAddEvent(medifitMetadata as ObjectNode, objectMapper, url.toString())
                        val capture_id = medifitClient.captureEventProxy(event)?:"Error"


                        call.response.header("Location", capture_id)
                        call.respond(event)
                    }
                } catch(ee:Exception){
                    call.respond("Error: $e $ee")
                }

            }
            finally {
                file?.delete()
            }
        }
        /*
         * Upload metadata from passed model file.
         * Body parameter: form data.
         * - file: Binary object
         */
        post("/captureModelEventFromFile") {
            val multipartData = call.receiveMultipart()

            var file: File? = null
            try {
                val url:URL = URL(call.request.header("url")?:"https://google.com")

                multipartData.forEachPart { part ->
                    // if part is a file (could be form item)
                    if (part is PartData.FileItem) {

                        // retrieve file name of upload
                        val name = part.originalFileName!!

                        val fileCopy = createTempFile(name).toFile()
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
                    val medifitMetadata = prepareEpcisBody(metadata, objectMapper)
                    val event = createEpcisAddEvent(medifitMetadata as ObjectNode, objectMapper, url.toString())
                    val capture_id = medifitClient.captureEvent(event)?:"Error"

                    call.response.header("Location", capture_id)

                    call.respond(event)
                }
            }catch(e:Exception){
                call.respond("Error: $e")
            }  finally {
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



                //call.respond(event)
                call.respond(capture_id)

            } catch (exception: Exception){
                try{
                    val model_id: String = executionEventParameters.model_id
                    val model_name: String = executionEventParameters.model_name
                    val event = createExecutionEventBody(model_id, model_name, objectMapper)
                    val capture_id = medifitClient.captureEventProxy(event)?:"Error"
                    call.respond(capture_id)
                }catch (exception2: Exception){
                    call.respond(exception2)
                }

            }

        }



    }


}

data class UrlJsonObject(val url: String)
data class ProxyObject(val proxyHost: String, val proxySocket:Int)
data class ExecutionEvent(val model_id: String, val model_name: String)
// on ice until outside connection is possible by internal servers


class MedifitClient(private val token: String) {

    private val url = "https://epcis.medifit-prima.net/"
    val appConfiguration :Properties = loadConfiguration()
    private val api_key = appConfiguration.getProperty("api-key")?:""
    private val api_key_secret = appConfiguration.getProperty("api-key-secret")?:""




    @InternalAPI
    suspend fun captureEvent(bodyParameter: JsonNode): String? {

        HttpClient(Apache)
        {
            engine {
                // this: ApacheEngineConfig
                followRedirects = true
                socketTimeout = 10_000
                connectTimeout = 10_000
                connectionRequestTimeout = 20_000
                customizeClient {
                    // this: HttpAsyncClientBuilder
                    //setProxy(HttpHost("webproxy", 8080))
                    setMaxConnTotal(1000)
                    setMaxConnPerRoute(100)
                    // ...
                }
                customizeRequest {
                    // this: RequestConfig.Builder
                }
            }
        }
            .use { client ->
            val response: HttpResponse = client.post("$url/capture") {
                headers {
                    //append(HttpHeaders.Authorization, token)
                    append(HttpHeaders.Connection,"keep-alive")
                    append(HttpHeaders.AccessControlAllowOrigin,"*/*")
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
    @InternalAPI
    suspend fun captureEventProxy(bodyParameter: JsonNode): String? {

        HttpClient(Apache)
        {
            engine {
                // this: ApacheEngineConfig
                followRedirects = true
                socketTimeout = 10_000
                connectTimeout = 10_000
                connectionRequestTimeout = 20_000
                customizeClient {
                    // this: HttpAsyncClientBuilder
                    setProxy(HttpHost(appHostname, appSocket))

                    setMaxConnTotal(1000)
                    setMaxConnPerRoute(100)
                    // ...
                }
                customizeRequest {
                    // this: RequestConfig.Builder
                }
            }
        }
            .use { client ->
                val response: HttpResponse = client.post("$url/capture") {
                    headers {
                        //append(HttpHeaders.Authorization, token)
                        append(HttpHeaders.Connection,"keep-alive")
                        append(HttpHeaders.AccessControlAllowOrigin,"*/*")
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
