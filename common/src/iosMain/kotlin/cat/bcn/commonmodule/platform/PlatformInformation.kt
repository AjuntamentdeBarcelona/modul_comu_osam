package cat.bcn.commonmodule.platform

import cat.bcn.commonmodule.model.Platform
import platform.Foundation.NSBundle
import platform.UIKit.UIDevice
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.alloc
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.ptr
import kotlinx.cinterop.reinterpret
import kotlinx.cinterop.sizeOf
import kotlinx.cinterop.value
import platform.SystemConfiguration.SCNetworkReachabilityCreateWithAddress
import platform.SystemConfiguration.SCNetworkReachabilityFlagsVar
import platform.SystemConfiguration.SCNetworkReachabilityGetFlags
import platform.SystemConfiguration.kSCNetworkReachabilityFlagsConnectionRequired
import platform.SystemConfiguration.kSCNetworkReachabilityFlagsReachable
import platform.posix.AF_INET
import platform.posix.sockaddr_in


@OptIn(ExperimentalForeignApi::class)
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
        return memScoped {
            val address = alloc<sockaddr_in>()
            address.sin_len = sizeOf<sockaddr_in>().toUByte()
            address.sin_family = AF_INET.toUByte()

            val reachability = SCNetworkReachabilityCreateWithAddress(null, address.ptr.reinterpret())
                ?: return@memScoped false

            val flags = alloc<SCNetworkReachabilityFlagsVar>()
            if (!SCNetworkReachabilityGetFlags(reachability, flags.ptr)) {
                return@memScoped false
            }

            val isReachable = flags.value.and(kSCNetworkReachabilityFlagsReachable) != 0u
            val needsConnection = flags.value.and(kSCNetworkReachabilityFlagsConnectionRequired) != 0u

            // A network is available if it's reachable and doesn't require a new connection
            // (e.g., a captive portal that needs a login).
            isReachable && !needsConnection
        }
    }
}