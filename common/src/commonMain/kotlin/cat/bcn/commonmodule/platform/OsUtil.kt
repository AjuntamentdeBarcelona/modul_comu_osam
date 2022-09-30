package cat.bcn.commonmodule.platform

interface OsUtil {
    fun encodeUrl(url: String): String?
    fun openUrl(url: String): Boolean
}