package cat.bcn.commonmodule.data.datasource.remote

import cat.bcn.commonmodule.model.Platform
import cat.bcn.commonmodule.model.Rating
import cat.bcn.commonmodule.model.Version
import cat.bcn.commonmodule.ui.versioncontrol.Language

internal interface Remote {
    suspend fun getVersion(
        appId: String,
        versionCode: Int,
        language: Language,
        platform: Platform
    ): Version

    suspend fun getRating(appId: String, platform: Platform): Rating
}
