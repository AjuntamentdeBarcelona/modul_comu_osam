package cat.bcn.commonmodule.data.datasource.remote

import cat.bcn.commonmodule.model.App
import cat.bcn.commonmodule.model.Version
import cat.bcn.commonmodule.ui.versioncontrol.Language

internal interface Remote {
    suspend fun getVersion(
        appId: String,
        versionCode: Int,
        language: Language,
        platform: App.Platform
    ): Version
}
