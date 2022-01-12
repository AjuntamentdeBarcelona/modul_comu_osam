package cat.bcn.commonmodule.data.datasource.remote

import cat.bcn.commonmodule.data.datasource.remote.client.buildClient
import cat.bcn.commonmodule.model.*
import cat.bcn.commonmodule.ui.versioncontrol.Language
import io.ktor.client.request.*
import io.ktor.utils.io.core.*


internal class CommonRemote(
    private val backendEndpoint: String
) : Remote {

    companion object {
        const val versionRoute = "api/version"
        const val ratingRoute = "api/rating"
    }

    override suspend fun getVersion(
        appId: String,
        versionCode: Int,
        language: Language,
        platform: Platform
    ): Version =
        buildClient(backendEndpoint).use {
            it.get<VersionResponse>("${versionRoute}/$appId/$platform/$versionCode")
        }.data

    override suspend fun getRating(appId: String, platform: Platform): Rating =
        buildClient(backendEndpoint).use {
            it.get<RatingResponse>("${ratingRoute}/$appId/$platform")
        }.data

}

