package cat.bcn.commonmodule.platform

interface PlatformUtil {
    fun encodeUrl(url: String): String?
    fun openUrl(url: String): Boolean
    fun getDeviceModelIdentifier(): String
}