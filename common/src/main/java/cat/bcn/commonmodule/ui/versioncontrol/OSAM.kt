package cat.bcn.commonmodule.ui.versioncontrol

import android.content.Context
import androidx.appcompat.app.AlertDialog
import cat.bcn.commonmodule.data.datasource.remote.CommonRemote
import cat.bcn.commonmodule.data.datasource.remote.Remote
import cat.bcn.commonmodule.model.Platform
import cat.bcn.commonmodule.model.Version
import cat.bcn.commonmodule.model.Version.ComparisonMode.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

actual class OSAM constructor(private val context: Context) {

    private val remote: Remote by lazy { CommonRemote() }

    actual fun versionControl(
        appId: String,
        versionCode: Int,
        language: Language,
        f: (VersionControlResponse) -> Unit
    ) {
        GlobalScope.launch {
            try {
                val version: Version =
                    remote.getVersion(appId, versionCode, language, Platform.ANDROID)

                withContext(Dispatchers.Main) {
                    val dialog = AlertDialog.Builder(context)
                        .setTitle(version.title(language))
                        .setMessage(version.message(language))
                        .setPositiveButton(version.ok(language)) { _, _ -> f(VersionControlResponse.ACCEPTED) }
                        .setOnDismissListener { f(VersionControlResponse.DISMISSED) }

                    when (version.comparisonMode) {
                        FORCE -> dialog.setCancelable(true)
                        LAZY -> dialog.setNegativeButton(version.cancel(language)) { _, _ ->
                            f(VersionControlResponse.CANCELLED)
                        }
                        INFO -> {
                            // Do nothing
                        }
                    }

                    dialog.show()
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) { f(VersionControlResponse.ERROR) }
            }
        }
    }

    actual fun rating(
        appId: String,
        f: (RatingControlResponse) -> Unit
    ) {
        try {
            GlobalScope.launch {
                remote.getRating(appId, Platform.ANDROID)
            }
        } catch (e: Exception) {
            f(RatingControlResponse.ERROR)
        }
    }
}
