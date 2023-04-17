package cat.bcn.commonmodule.data.datasource.remote.client

import cat.bcn.commonmodule.extensions.isDebug
import cat.bcn.commonmodule.performance.PerformanceMetric
import io.ktor.client.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
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
            val jsonCustom = Json {
                isLenient = true
                ignoreUnknownKeys = true
                allowSpecialFloatingPointValues = true
                useArrayPolymorphism = true
            }
            json(jsonCustom)
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

    client.receivePipeline.intercept(HttpReceivePipeline.After) { response ->
        val contentTypeResponse = try {
            response.contentType()
        } catch (t: Throwable) {
            null
        }
        val contentLengthRequest = try {
            response.request.contentLength()
        } catch (t: Throwable) {
            null
        }
        val contentLengthResponse = try {
            response.contentLength()
        } catch (t: Throwable) {
            null
        }
        val httpStatusResponse = response.status.value
        contentTypeResponse?.also { contentTypeResponse ->
            metric?.setResponseContentType("${contentTypeResponse.contentType}/${contentTypeResponse.contentSubtype}")
        }
        contentLengthRequest?.also { contentLengthRequest ->
            metric?.setRequestPayloadSize(contentLengthRequest)
        }
        contentLengthResponse?.also { contentLengthResponse ->
            metric?.setResponsePayloadSize(contentLengthResponse)
        }
        response.request.headers.entries().forEach { entry ->
            metric?.putAttribute("requestHeaderKey:${entry.key}", "requestHeaderValue:${entry.value}")
        }
        response.headers.entries().forEach { entry ->
            metric?.putAttribute("responseHeaderKey:${entry.key}", "responseHeaderValue:${entry.value}")
        }
        metric?.setHttpResponseCode(httpStatusResponse)
        metric?.stop()
        proceed()
    }


    return client
}
