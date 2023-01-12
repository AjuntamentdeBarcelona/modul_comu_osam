package cat.bcn.commonmodule.platform

import cat.bcn.commonmodule.model.Platform
import platform.Foundation.NSBundle
import platform.UIKit.UIDevice

internal actual class PlatformInformation {

    private val appsStoreId = "" // TODO not necessary for the moment

    actual fun getPlatform(): Platform = Platform.IOS

    actual fun getPlatformName(): String = UIDevice.currentDevice.systemName()

    actual fun getPlatformVersion(): String = UIDevice.currentDevice.systemVersion()

    actual fun getPlatformModel(platformUtil: PlatformUtil): String {
        return platformUtil.getDeviceModelIdentifier()
    }

    actual fun getPackageName(): String = NSBundle.mainBundle.bundleIdentifier!!

    actual fun getVersionCode(): Long =
        (NSBundle.mainBundle.infoDictionary!!["CFBundleVersion"] as String).toLong()

    actual fun getVersionName(): String =
        NSBundle.mainBundle.infoDictionary!!["CFBundleShortVersionString"] as String

    actual fun getAppName(): String = NSBundle.mainBundle.infoDictionary!!["CFBundleName"] as String

    actual fun getAppsStoreUrl(): String = "itms-apps://itunes.apple.com/app/$appsStoreId"

    actual fun isOnline(): Boolean {
        //Para futuro. Es complicado meterlo aqui. La opcion seria hacer un PING.
        //Por ahora, si no hay conexion entra en el catch y sigue la ejecucion como si estuviera offline
        return true
    }

}