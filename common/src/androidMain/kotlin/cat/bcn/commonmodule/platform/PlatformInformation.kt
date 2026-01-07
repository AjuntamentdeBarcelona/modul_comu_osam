package cat.bcn.commonmodule.platform

import android.content.Context
import android.content.pm.PackageInfo
import android.net.ConnectivityManager
import android.net.NetworkCapabilities.*
import android.os.Build
import cat.bcn.commonmodule.model.Platform
import cat.bcn.commonmodule.testing.Mockable
import java.util.Locale

@Mockable
internal actual class PlatformInformation(private val context: Context) {

    actual fun getPlatform(): Platform = Platform.ANDROID

    actual fun getPlatformName(): String = "android"

    actual fun getPlatformVersion(): String = Build.VERSION.RELEASE

    actual fun getPlatformModel(platformUtil: PlatformUtil): String = Build.BRAND + " " + Build.MANUFACTURER + " " + Build.MODEL

    actual fun getPackageName(): String = context.packageName

    actual fun getVersionCode(): Long = getPackageInfo().versionCode.toLong()

    actual fun getVersionName(): String = getPackageInfo().versionName ?: "unknown"

    actual fun getAppName(): String = context.applicationInfo.loadLabel(context.packageManager).toString()

    actual fun getAppsStoreUrl(): String =
        "https://play.google.com/store/apps/details?id=${getPackageName()}"

    actual fun isOnline(): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
                ?.let { capabilities ->
                    setOf(
                        TRANSPORT_CELLULAR,
                        TRANSPORT_WIFI,
                        TRANSPORT_ETHERNET
                    ).map { capabilities.hasTransport(it) }.reduce(Boolean::or)
                } ?: false
        } else {
            val activeNetworkInfo = connectivityManager.activeNetworkInfo
            activeNetworkInfo != null && activeNetworkInfo.isConnected
        }
    }

    private fun getPackageInfo(): PackageInfo =
        context.packageManager.getPackageInfo(context.packageName, 0)

    actual fun getDeviceLanguage(): String {
        return Locale.getDefault().language.uppercase(Locale.ROOT)
    }

    actual fun getSmallPackageName(): String {
        val packageName = context.packageName
        return packageName.split(".").last()
    }
}