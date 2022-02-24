package cat.bcn.commonmodule.data.datasource.remote

import cat.bcn.commonmodule.model.Platform
import cat.bcn.commonmodule.model.Rating
import cat.bcn.commonmodule.model.Version

internal interface Remote {
    suspend fun getVersion(appId: String, platform: Platform, versionCode: Long): Version
    suspend fun getRating(appId: String, platform: Platform): Rating
}
