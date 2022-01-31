package cat.bcn.commonmodule.platform

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.core.content.ContextCompat

internal actual class PlatformAction(private val context: Context) {
    actual fun openUrl(url: String) {
        val intent = Intent(Intent.ACTION_VIEW)
        intent.data = Uri.parse(url)
        ContextCompat.startActivity(context, intent, null)
    }
}