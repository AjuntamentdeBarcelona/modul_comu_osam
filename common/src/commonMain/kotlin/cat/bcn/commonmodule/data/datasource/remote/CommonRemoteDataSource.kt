package cat.bcn.commonmodule.data.datasource.remote

import cat.bcn.commonmodule.data.datasource.models.dto.RatingResponseDto
import cat.bcn.commonmodule.data.datasource.models.dto.VersionResponseDto
import cat.bcn.commonmodule.data.datasource.remote.client.buildClient
import cat.bcn.commonmodule.data.mapper.dto.toModel
import cat.bcn.commonmodule.model.Platform
import cat.bcn.commonmodule.model.Rating
import cat.bcn.commonmodule.model.Version
import cat.bcn.commonmodule.performance.InternalPerformanceWrapper
import cat.bcn.commonmodule.performance.PerformanceMetric
import io.ktor.client.request.*
import io.ktor.utils.io.core.*

internal class CommonRemote(
    private val backendEndpoint: String
) : Remote {

    companion object {
        const val versionRoute = "api/version"
        const val ratingRoute = "api/rating"
    }

    override suspend fun getVersion(performance: InternalPerformanceWrapper, appId: String, platform: Platform, versionCode: Long): Version =
        buildClient(backendEndpoint, createMetricCreator(performance)).use {
            it.get<VersionResponseDto>("${versionRoute}/$appId/$platform/$versionCode")
        }.data.toModel()

    override suspend fun getRating(performance: InternalPerformanceWrapper, appId: String, platform: Platform): Rating =
        buildClient(backendEndpoint, createMetricCreator(performance)).use {
            it.get<RatingResponseDto>("${ratingRoute}/$appId/$platform")
        }.data.toModel()

    private fun createMetricCreator(performance: InternalPerformanceWrapper): (url: String, httpMethod: String) -> PerformanceMetric {
        return { url, httpMethod -> performance.createMetric(url, httpMethod)}
    }

}

