package cat.bcn.commonmodule.data.datasource.remote

import cat.bcn.commonmodule.constants.Constants
import cat.bcn.commonmodule.data.datasource.remote.client.buildClient
import cat.bcn.commonmodule.model.*
import cat.bcn.commonmodule.ui.versioncontrol.Language
import io.ktor.client.request.*
import io.ktor.utils.io.core.*


internal class CommonRemote(
) : Remote {

    override suspend fun getVersion(
        appId: String,
        versionCode: Int,
        language: Language,
        platform: Platform
    ): Version =
        buildClient(Constants.BACKEND_ENDPOINT).use {
            it.get<VersionResponse>("${Constants.VERSION_ROUTE}/$appId/$platform/$versionCode")
        }.data

    override suspend fun getRating(appId: String, platform: Platform): Rating =
        buildClient(Constants.BACKEND_ENDPOINT).use {
            it.get<RatingResponse>("${Constants.RATING_ROUTE}/$appId/$platform")
        }.data

}

