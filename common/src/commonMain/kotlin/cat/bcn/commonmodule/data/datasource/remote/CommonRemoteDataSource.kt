package cat.bcn.commonmodule.data.datasource.remote

import cat.bcn.commonmodule.constants.Constants
import cat.bcn.commonmodule.data.datasource.remote.client.buildClient
import cat.bcn.commonmodule.model.App
import cat.bcn.commonmodule.model.Version
import cat.bcn.commonmodule.model.VersionResponse
import cat.bcn.commonmodule.ui.versioncontrol.Language
import co.touchlab.stately.ensureNeverFrozen
import io.ktor.client.request.*
import io.ktor.utils.io.core.*


internal class CommonRemote(
) : Remote {

    init {
        ensureNeverFrozen()
    }

    override suspend fun getVersion(
        appId: String,
        versionCode: Int,
        language: Language,
        platform: App.Platform
    ): Version =
        buildClient(Constants.VERSION_CONTROL_ENDPOINT).use {
            it.get<VersionResponse>("${Constants.VERSION_ROUTE}/$appId/$platform/$versionCode")
        }.data

}

