package cat.bcn.commonmodule.platform

import cat.bcn.commonmodule.model.Platform

internal expect class PlatformInformation {
    fun getPlatform(): Platform
    fun getPackageName(): String
    fun getVersionCode(): Long
    fun getVersionName(): String
    fun getAppsStoreUrl(): String
    fun isOnline(): Boolean
}