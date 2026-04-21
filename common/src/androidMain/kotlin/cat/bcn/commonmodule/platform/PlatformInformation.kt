package cat.bcn.commonmodule.platform

import android.content.Context
import android.content.pm.PackageInfo
import android.net.ConnectivityManager
import android.net.NetworkCapabilities.*
import android.os.Build
import android.provider.Settings
import cat.bcn.commonmodule.model.InternetNoConnection
import cat.bcn.commonmodule.model.Platform
import cat.bcn.commonmodule.testing.Mockable
import java.util.Locale

@Mockable
internal actual class PlatformInformation(private val context: Context) {

    actual fun getPlatform(): Platform = Platform.ANDROID

    actual fun getPlatformName(): String = "android"

    actual fun getPlatformVersion(): String = Build.VERSION.RELEASE

    actual fun getPlatformModel(platformUtil: PlatformUtil): String = Build.BRAND + " " + Build.MANUFACTURER + " " + Build.MODEL

    actual fun getDeviceModel(platformUtil: PlatformUtil): String  = Build.MODEL

    actual fun getPackageName(): String = context.packageName

    actual fun getVersionCode(): Long = getPackageInfo().versionCode.toLong()

    actual fun getVersionName(): String = getPackageInfo().versionName ?: "unknown"

    actual fun getAppName(): String = context.applicationInfo.loadLabel(context.packageManager).toString()

    actual fun getAppsStoreUrl(): String =
        "https://play.google.com/store/apps/details?id=${getPackageName()}"

    actual fun isOnline(): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val isConnected = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
                ?.let { capabilities ->
                    setOf(
                        TRANSPORT_CELLULAR,
                        TRANSPORT_WIFI,
                        TRANSPORT_ETHERNET
                    ).any { capabilities.hasTransport(it) }
                } ?: false
        } else {
            val activeNetworkInfo = connectivityManager.activeNetworkInfo
            activeNetworkInfo != null && activeNetworkInfo.isConnected
        }

        return if (isConnected) {
            pingEndpoint("www.google.com")
        } else {
            false
        }
    }

    private fun pingEndpoint(host: String): Boolean {
        return try {
            val process = Runtime.getRuntime().exec("ping -c 1 -W 1 $host")
            process.waitFor() == 0
        } catch (e: Exception) {
            false
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

    actual fun getNoConnectionType(): InternetNoConnection {
        return if (isAirplaneModeOn()) {
            InternetNoConnection.AIRPLANE_MODE
        } else {
            InternetNoConnection.NO_WIFI
        }
    }

    private fun isAirplaneModeOn(): Boolean {
        return Settings.Global.getInt(
            context.contentResolver,
            Settings.Global.AIRPLANE_MODE_ON, 0
        ) != 0
    }
}