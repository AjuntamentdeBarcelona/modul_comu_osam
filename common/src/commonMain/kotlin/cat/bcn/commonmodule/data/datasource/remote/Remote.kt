package cat.bcn.commonmodule.data.datasource.remote

import cat.bcn.commonmodule.model.Platform
import cat.bcn.commonmodule.model.Rating
import cat.bcn.commonmodule.model.Version
import cat.bcn.commonmodule.performance.InternalPerformanceWrapper

internal interface Remote {
    suspend fun getVersion(performance: InternalPerformanceWrapper, appId: String, platform: Platform, versionCode: Long): Version
    suspend fun getRating(performance: InternalPerformanceWrapper, appId: String, platform: Platform): Rating
}
