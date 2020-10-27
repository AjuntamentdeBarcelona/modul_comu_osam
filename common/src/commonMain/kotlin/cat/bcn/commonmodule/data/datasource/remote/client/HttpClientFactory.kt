package cat.bcn.commonmodule.data.datasource.remote.client

import cat.bcn.commonmodule.data.datasource.local.Preferences
import cat.bcn.commonmodule.data.datasource.remote.TokenFeature
import io.ktor.client.*
import io.ktor.client.features.*
import io.ktor.client.features.auth.*
import io.ktor.client.features.auth.providers.*
import io.ktor.client.features.json.*
import io.ktor.client.features.json.serializer.*
import io.ktor.client.features.logging.*
import io.ktor.http.*

fun buildClient(endpoint: String, block: HttpClientConfig<*>.() -> Unit = {}): HttpClient =
    HttpClient {
        defaultRequest {
            val endpointUrlBuilder = URLBuilder(endpoint)
            url {
                protocol = endpointUrlBuilder.protocol
                host = endpointUrlBuilder.host
            }
        }
        install(Logging) {
            logger = Logger.SIMPLE
            level = LogLevel.ALL
        }
        install(JsonFeature) {
            serializer = KotlinxSerializer(json)
        }

        install(Auth) {
            basic {
                username = "osam"
                password = "osam"
            }
        }

        block(this)
    }


fun buildClientWithToken(endpoint: String, tokenProvider: Preferences): HttpClient =
    buildClient(endpoint) {
        install(TokenFeature) {
            this.tokenProvider = tokenProvider
        }
    }
