package cat.bcn.commonmodule.data.datasource.remote

import cat.bcn.commonmodule.data.datasource.models.dto.RatingResponseDto
import cat.bcn.commonmodule.data.datasource.models.dto.VersionResponseDto
import cat.bcn.commonmodule.data.datasource.remote.client.buildClient
import cat.bcn.commonmodule.data.mapper.dto.toModel
import cat.bcn.commonmodule.model.BackendTimeoutException
import cat.bcn.commonmodule.model.Platform
import cat.bcn.commonmodule.model.Rating
import cat.bcn.commonmodule.model.Version
import cat.bcn.commonmodule.performance.InternalPerformanceWrapper
import cat.bcn.commonmodule.performance.PerformanceMetric
import io.ktor.client.call.body
import io.ktor.client.network.sockets.ConnectTimeoutException
import io.ktor.client.network.sockets.SocketTimeoutException
import io.ktor.client.plugins.HttpRequestTimeoutException
import io.ktor.client.request.*
import io.ktor.utils.io.core.*

internal class CommonRemote(
    private val backendEndpoint: String
) : Remote {

    companion object {
        const val versionRoute = "api/version"
        const val ratingRoute = "api/rating"
    }

    override suspend fun getVersion(performance: InternalPerformanceWrapper, appId: String, platform: Platform, versionCode: Long): Version {
        val path = "${versionRoute}/$appId/$platform/$versionCode"
        val url =
            backendEndpoint.let { if (it.endsWith("/")) it else "$it/" } + path.let { if (path.startsWith("/") && path.length > 1) path.substring(1) else path }
        val httpMethod = "get"
        try {
            // Added 'return' here
            return buildClient(backendEndpoint, createMetricCreator(performance, url, httpMethod)).use {
                it.get(path).body<VersionResponseDto>().data.toModel()
            }
        } catch (e: ConnectTimeoutException) {
            println("BackendTimeoutException: ${e.message}")
            throw BackendTimeoutException(env = backendEndpoint, cause = e)
        } catch (e: SocketTimeoutException) {
            println("BackendTimeoutException: ${e.message}")
            throw BackendTimeoutException(env = backendEndpoint, cause = e)
        } catch (e: HttpRequestTimeoutException){
            println("BackendTimeoutException: ${e.message}")
            throw BackendTimeoutException(env = backendEndpoint, cause = e)
        }
    }

    override suspend fun getRating(performance: InternalPerformanceWrapper, appId: String, platform: Platform): Rating {
        val path = "${ratingRoute}/$appId/$platform"
        val httpMethod = "get"
        val url = backendEndpoint.let { if(it.endsWith("/")) it else "$it/" } + path.let { if(path.startsWith("/") && path.length > 1) path.substring(1) else path }
        return buildClient(backendEndpoint, createMetricCreator(performance, url, httpMethod)).use {
            it.get(path).body<RatingResponseDto>().data.toModel()
        }
    }

    private fun createMetricCreator(performance: InternalPerformanceWrapper, url: String, httpMethod: String): PerformanceMetric? {
        return performance.createMetric(url, httpMethod)
    }

}

