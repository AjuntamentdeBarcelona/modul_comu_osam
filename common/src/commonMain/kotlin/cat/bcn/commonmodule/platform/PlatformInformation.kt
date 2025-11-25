package cat.bcn.commonmodule.platform

import cat.bcn.commonmodule.model.Platform

internal expect class PlatformInformation {
    fun getPlatform(): Platform
    fun getPlatformName(): String
    fun getPlatformVersion(): String
    fun getPlatformModel(platformUtil: PlatformUtil): String
    fun getPackageName(): String
    fun getVersionCode(): Long
    fun getVersionName(): String
    fun getAppName(): String
    fun getAppsStoreUrl(): String
    fun isOnline(): Boolean
    fun getDeviceLanguage(): String
    fun getSmallPackageName(): String
}