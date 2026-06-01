package cat.bcn.commonmodule.data.datasource.remote.client

import cat.bcn.commonmodule.extensions.isDebug
import cat.bcn.commonmodule.performance.PerformanceMetric
import io.ktor.client.HttpClient
import io.ktor.client.HttpClientConfig
import io.ktor.client.plugins.HttpRequestRetry
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.plugins.logging.SIMPLE
import io.ktor.client.request.HttpSendPipeline
import io.ktor.client.request.header
import io.ktor.client.statement.HttpReceivePipeline
import io.ktor.client.statement.request
import io.ktor.http.URLBuilder
import io.ktor.http.contentLength
import io.ktor.http.contentType
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
        install(HttpTimeout) {
            connectTimeoutMillis = 10_000
            socketTimeoutMillis = 10_000
            requestTimeoutMillis = 15_000
        }
        install(HttpRequestRetry) {
            // Retry transient connectivity failures (DNS/connect/socket) and 5xx.
            // GET endpoints (api/version, api/rating) are idempotent, so this is safe.
            retryOnExceptionOrServerErrors(maxRetries = 2)
            exponentialDelay(maxDelayMs = 3_000)
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
    // HttpRequestRetry re-runs the send pipeline on every retry attempt, so guard
    // the metric so it is started only once per request (each client instance
    // serves a single request). Avoids "metric already started" warnings while
    // still measuring the full duration, retries included.
    var metricStarted = false
    client.sendPipeline.intercept(HttpSendPipeline.Before) {
        if (!metricStarted) {
            metric?.start()
            metricStarted = true
        }
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
            metric?.putAttribute(
                "requestHeaderKey:${entry.key}",
                "requestHeaderValue:${entry.value}"
            )
        }
        phase.headers.entries().forEach { entry ->
            metric?.putAttribute(
                "responseHeaderKey:${entry.key}",
                "responseHeaderValue:${entry.value}"
            )
        }
        metric?.setHttpResponseCode(httpStatusResponse)
        metric?.stop()
        proceed()
    }


    return client
}
