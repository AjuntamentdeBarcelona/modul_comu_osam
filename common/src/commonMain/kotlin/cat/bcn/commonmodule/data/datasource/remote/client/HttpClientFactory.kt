package cat.bcn.commonmodule.data.datasource.remote.client

import io.ktor.client.*
import io.ktor.client.features.*
import io.ktor.client.features.json.*
import io.ktor.client.features.json.serializer.*
import io.ktor.client.features.logging.*
import io.ktor.client.request.*
import io.ktor.http.*

fun buildClient(endpoint: String, block: HttpClientConfig<*>.() -> Unit = {}): HttpClient =
    HttpClient {
        defaultRequest {
            val endpointUrlBuilder = URLBuilder(endpoint)
            url {
                protocol = endpointUrlBuilder.protocol
                host = endpointUrlBuilder.host
            }

            header("Authorization", "Basic b3NhbTpvc2Ft")
        }
        install(Logging) {
            logger = Logger.SIMPLE
            level = LogLevel.ALL
        }
        install(JsonFeature) {
            serializer = KotlinxSerializer()
        }

        block(this)
    }
