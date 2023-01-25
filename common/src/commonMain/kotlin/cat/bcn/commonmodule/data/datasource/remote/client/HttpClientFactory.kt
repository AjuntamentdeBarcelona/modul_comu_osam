package cat.bcn.commonmodule.data.datasource.remote.client

import cat.bcn.commonmodule.extensions.isDebug
import cat.bcn.commonmodule.performance.PerformanceMetric
import io.ktor.client.*
import io.ktor.client.features.*
import io.ktor.client.features.json.*
import io.ktor.client.features.json.serializer.*
import io.ktor.client.features.logging.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*

fun buildClient(
    endpoint: String,
    metricCreator: (url: String, httpMethod: String) -> PerformanceMetric,
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
        install(JsonFeature) {
            serializer = KotlinxSerializer(kotlinx.serialization.json.Json {
                ignoreUnknownKeys = true
            })
        }
        block(this)
    }
    var metric: PerformanceMetric? = null

    client.sendPipeline.intercept(HttpSendPipeline.Before) {
        val url = context.url.buildString()
        val httpMethod = context.method.value
        metric = metricCreator(url, httpMethod)
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

    client.receivePipeline.intercept(HttpReceivePipeline.After) {
        val contentTypeResponse = try {
            context.response.contentType()
        } catch (t: Throwable) {
            null
        }
        val contentLengthRequest = try {
            context.request.contentLength()
        } catch (t: Throwable) {
            null
        }
        val contentLengthResponse = try {
            context.response.contentLength()
        } catch (t: Throwable) {
            null
        }
        val httpStatusResponse = context.response.status.value
        contentTypeResponse?.also { contentTypeResponse ->
            metric?.setResponseContentType("${contentTypeResponse.contentType}/${contentTypeResponse.contentSubtype}")
        }
        contentLengthRequest?.also { contentLengthRequest ->
            metric?.setRequestPayloadSize("$contentLengthRequest")
        }
        contentLengthResponse?.also { contentLengthResponse ->
            metric?.setResponsePayloadSize("$contentLengthResponse")
        }
        context.request.headers.entries().forEach { entry ->
            metric?.putAttribute("requestHeaderKey:${entry.key}", "requestHeaderValue:${entry.value}")
        }
        context.response.headers.entries().forEach { entry ->
            metric?.putAttribute("responseHeaderKey:${entry.key}", "responseHeaderValue:${entry.value}")
        }
        metric?.setHttpResponseCode(httpStatusResponse)
        metric?.stop()
        proceed()
    }


    return client
}
