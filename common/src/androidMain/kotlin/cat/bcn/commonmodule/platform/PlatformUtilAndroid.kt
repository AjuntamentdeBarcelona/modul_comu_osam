package cat.bcn.commonmodule.platform

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.core.content.ContextCompat

class PlatformUtilAndroid(private val context: Context) : PlatformUtil {
    override fun encodeUrl(url: String): String? {
        return url
    }

    override fun openUrl(url: String): Boolean {
        val intent = Intent(Intent.ACTION_VIEW)
        intent.data = Uri.parse(url)
        ContextCompat.startActivity(context, intent, null)
        return true
    }

    override fun getDeviceModelIdentifier(): String {
        return ""
    }
}