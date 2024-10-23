package cat.bcn.commonmodule.data.datasource.remote.client

import cat.bcn.commonmodule.extensions.isDebug
import cat.bcn.commonmodule.performance.PerformanceMetric
import io.ktor.client.*
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.plugins.logging.SIMPLE
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

fun buildClient(
    endpoint: String,
    metric: PerformanceMetric?,
    block: HttpClientConfig<*>.() -> Unit = {}
): HttpClient {
    val client = HttpClient {
        defaultRequest {
            val endpointUrlBuilder = URLBuilder(endpoint)
            url {
                protocol = endpointUrlBuilder.protocol
                host = endpointUrlBuilder.host
            }

            header("Authorization", "Basic b3NhbTpvc2Ft")
        }
        if (isDebug) {
            install(Logging) {
                logger = Logger.SIMPLE
                level = LogLevel.ALL
            }
        }
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
            })
        }
        block(this)
    }
    client.sendPipeline.intercept(HttpSendPipeline.Before) {
        val url = context.url.buildString()
        val httpMethod = context.method.value
        metric?.start()
        proceed()
    }
    client.sendPipeline.intercept(HttpSendPipeline.Engine) {
        metric?.markRequestComplete()
        proceed()
    }
    client.receivePipeline.intercept(HttpReceivePipeline.Before) {
        metric?.markResponseStart()
        proceed()
    }

    client.receivePipeline.intercept(HttpReceivePipeline.After) { phase ->
        val contentTypeResponse = try {
            phase.contentType()
        } catch (t: Throwable) {
            null
        }
        val contentLengthRequest = try {
            phase.request.contentLength()
        } catch (t: Throwable) {
            null
        }
        val contentLengthResponse = try {
            phase.contentLength()
        } catch (t: Throwable) {
            null
        }
        val httpStatusResponse = phase.status.value
        contentTypeResponse?.also { contentTypeResponse ->
            metric?.setResponseContentType("${contentTypeResponse.contentType}/${contentTypeResponse.contentSubtype}")
        }
        contentLengthRequest?.also { contentLengthRequest ->
            metric?.setRequestPayloadSize(contentLengthRequest)
        }
        contentLengthResponse?.also { contentLengthResponse ->
            metric?.setResponsePayloadSize(contentLengthResponse)
        }
        phase.request.headers.entries().forEach { entry ->
            metric?.putAttribute("requestHeaderKey:${entry.key}", "requestHeaderValue:${entry.value}")
        }
        phase.headers.entries().forEach { entry ->
            metric?.putAttribute("responseHeaderKey:${entry.key}", "responseHeaderValue:${entry.value}")
        }
        metric?.setHttpResponseCode(httpStatusResponse)
        metric?.stop()
        proceed()
    }


    return client
}
