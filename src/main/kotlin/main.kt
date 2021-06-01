import com.google.gson.*
import io.ktor.application.*
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.features.*
import io.ktor.http.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.gson.*

suspend fun main(args: Array<String>) {

    val client = HttpClient()

    embeddedServer(Netty, port = 8000) {

        install(ContentNegotiation) {
            gson()
        }

        routing {
            get("/") {
                call.respondText("Hello, world!")
            }

            get("/models") {
                call.respond(getModels(client))
            }
        }
    }.start(wait = true)
}

private suspend fun getModels(client: HttpClient): List<JsonElement> {
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
                        "ExposureModel",
                        "PredictiveModel"
                    ]
                }
            }
        """.trimIndent()
    }

    if (response.status == HttpStatusCode.OK) {
        val models = JsonParser.parseString(response.readText())

        // eventList is an array of models where the child fsk:model holds metadata
        val eventList = (models as JsonObject).get("eventList") as JsonArray

        return eventList.map {
            val currentObject = it as JsonObject
            currentObject.get("fsk:model")
        }
    }

    return emptyList<JsonElement>()
}
