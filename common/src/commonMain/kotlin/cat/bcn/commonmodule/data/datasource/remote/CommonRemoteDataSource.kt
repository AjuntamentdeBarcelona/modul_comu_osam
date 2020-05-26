package cat.bcn.commonmodule.data.datasource.remote

import cat.bcn.commonmodule.data.datasource.local.Preferences
import cat.bcn.commonmodule.data.datasource.remote.client.buildClientWithToken
import co.touchlab.stately.ensureNeverFrozen


class CommonRemoteDataSource(
    private val preferences: Preferences,
    private val endPoint: String
) : RemoteDataSource {

    private val client = buildClientWithToken(endPoint, preferences)

    init {
        ensureNeverFrozen()
    }

    private fun Map<String, Any>.buildParams() = this.map { "${it.key}=${it.value}" }.joinToString("&")
}

