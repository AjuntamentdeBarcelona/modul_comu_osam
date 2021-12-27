package cat.bcn.commonmodule.data.datasource.remote

import cat.bcn.commonmodule.data.datasource.remote.client.buildClient
import cat.bcn.commonmodule.model.*
import cat.bcn.commonmodule.ui.versioncontrol.Language
import cat.bcn.commonmodule.ui.versioncontrol.OSAMEnvironment
import io.ktor.client.request.*
import io.ktor.utils.io.core.*


internal class CommonRemote(
    private val environment: OSAMEnvironment
) : Remote {

    override suspend fun getVersion(
        appId: String,
        versionCode: Int,
        language: Language,
        platform: Platform
    ): Version =
        buildClient(environment.backendEndpoint).use {
            it.get<VersionResponse>("${environment.versionRoute}/$appId/$platform/$versionCode")
        }.data

    override suspend fun getRating(appId: String, platform: Platform): Rating =
        buildClient(environment.backendEndpoint).use {
            it.get<RatingResponse>("${environment.ratingRoute}/$appId/$platform")
        }.data

}

