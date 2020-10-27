package cat.bcn.commonmodule.ui.versioncontrol

import android.content.Context
import android.util.Log
import androidx.appcompat.app.AlertDialog
import cat.bcn.commonmodule.data.datasource.remote.CommonRemote
import cat.bcn.commonmodule.model.App
import cat.bcn.commonmodule.model.Version
import cat.bcn.commonmodule.model.Version.ComparisonMode.*
import cat.bcn.commonmodule.ui.versioncontrol.VersionControlResponse.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

actual class VersionControl constructor(private val context: Context) {

    actual fun check(
        appId: String,
        versionCode: Int,
        language: Language,
        f: (VersionControlResponse) -> Unit
    ) {
        GlobalScope.launch {
            try {
                val version: Version =
                    CommonRemote().getVersion(appId, versionCode, language, App.Platform.ANDROID)

                withContext(Dispatchers.Main) {
                    val dialog = AlertDialog.Builder(context)
                        .setTitle(version.title(language))
                        .setMessage(version.message(language))
                        .setPositiveButton(version.ok(language)) { _, _ -> f(ACCEPTED) }
                        .setOnDismissListener { f(DISMISSED) }

                    when (version.comparisonMode) {
                        FORCE -> dialog.setCancelable(true)
                        LAZY -> dialog.setNegativeButton(version.cancel(language)) { _, _ ->
                            f(
                                CANCELLED
                            )
                        }
                        INFO -> {
                            // Do nothing
                        }
                    }

                    dialog.show()
                }
            } catch (e: Exception) {
                Log.e("VersionControl", "Exception error", e)
                withContext(Dispatchers.Main) { f(ERROR) }
            }
        }
    }
}
