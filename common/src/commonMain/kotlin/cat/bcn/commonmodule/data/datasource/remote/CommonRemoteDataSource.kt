package cat.bcn.commonmodule.data.datasource.remote

import cat.bcn.commonmodule.data.datasource.models.dto.RatingResponseDto
import cat.bcn.commonmodule.data.datasource.models.dto.VersionResponseDto
import cat.bcn.commonmodule.data.datasource.remote.client.buildClient
import cat.bcn.commonmodule.data.mapper.dto.toModel
import cat.bcn.commonmodule.model.Platform
import cat.bcn.commonmodule.model.Rating
import cat.bcn.commonmodule.model.Version
import io.ktor.client.request.*
import io.ktor.utils.io.core.*

internal class CommonRemote(
    private val backendEndpoint: String
) : Remote {

    companion object {
        const val versionRoute = "api/version"
        const val ratingRoute = "api/rating"
    }

    override suspend fun getVersion(appId: String, platform: Platform, versionCode: Long): Version =
        buildClient(backendEndpoint).use {
            it.get<VersionResponseDto>("${versionRoute}/$appId/$platform/$versionCode")
        }.data.toModel()

    override suspend fun getRating(appId: String, platform: Platform): Rating =
        buildClient(backendEndpoint).use {
            it.get<RatingResponseDto>("${ratingRoute}/$appId/$platform")
        }.data.toModel()

}

