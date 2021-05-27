package cat.bcn.commonmodule.ui.versioncontrol

import android.content.Context
import androidx.appcompat.app.AlertDialog
import cat.bcn.commonmodule.analytics.Analytics
import cat.bcn.commonmodule.analytics.CommonAnalytics
import cat.bcn.commonmodule.data.datasource.local.CommonPreferences
import cat.bcn.commonmodule.data.datasource.local.Preferences
import cat.bcn.commonmodule.data.datasource.remote.CommonRemote
import cat.bcn.commonmodule.data.datasource.remote.Remote
import cat.bcn.commonmodule.data.datasource.settings.Settings
import cat.bcn.commonmodule.model.Platform
import cat.bcn.commonmodule.model.Version
import cat.bcn.commonmodule.model.Version.ComparisonMode.*
import cat.bcn.commonmodule.model.localize
import com.google.firebase.analytics.FirebaseAnalytics
import com.soywiz.klock.DateTime
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

actual class OSAMCommons constructor(private val context: Context) {

    private val analytics: Analytics by lazy { CommonAnalytics(context) }
    private val remote: Remote by lazy { CommonRemote() }
    private val preferences: Preferences by lazy { CommonPreferences(Settings("default", context)) }
    private val EVENT_ID = "osamcommons"
    private val VERSION_CONTROL_POPUP = "version_control_popup_showed"
    private val RATING_POPUP = "rating_popup_showed"

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
                        .setTitle(version.title.localize(language))
                        .setMessage(version.message.localize(language))
                        .setPositiveButton(version.ok.localize(language)) { _, _ ->
                            f(VersionControlResponse.ACCEPTED)
                            //Launch intent to version.url
                        }


                    when (version.comparisonMode) {
                        FORCE -> dialog.setCancelable(false)
                        LAZY -> {
                            dialog.setNegativeButton(version.cancel.localize(language)) { _, _ ->
                                f(VersionControlResponse.CANCELLED)
                            }
                                .setOnDismissListener { f(VersionControlResponse.DISMISSED) }
                        }
                        INFO -> {
                            // Do nothing
                        }
                    }

                    dialog.show()
                    analytics.logVersionControlPopUp(
                        params = mapOf(FirebaseAnalytics.Param.ITEM_ID to EVENT_ID, FirebaseAnalytics.Param.ITEM_NAME to VERSION_CONTROL_POPUP)
                    )
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) { f(VersionControlResponse.ERROR) }
            }
        }
    }

    actual fun rating(
        appId: String,
        language: Language,
        f: (RatingControlResponse) -> Unit
    ) {
        try {
            GlobalScope.launch {
                val rating = remote.getRating(appId, Platform.ANDROID)

                val shouldShowRatingDialog = shouldShowRatingDialog(
                    rating = rating,
                    lastDatetime = preferences.getLastDatetime(),
                    numAperture = preferences.getNumApertures()
                )

                if (shouldShowRatingDialog) {
                    withContext(Dispatchers.Main) {
                        val dialog = AlertDialog.Builder(context)
                            .setTitle("Control de rating title")
                            .setMessage(rating.message.localize(language))
                            .setPositiveButton("Aceptar") { _, _ -> f(RatingControlResponse.ACCEPTED) }
                            .setOnDismissListener { f(RatingControlResponse.ACCEPTED) }
                            .setNegativeButton("Cancelar") { _, _ -> f(RatingControlResponse.ACCEPTED) }
                            .create()

                        dialog.show()
                        analytics.logRatingPopUp(
                            params = mapOf(FirebaseAnalytics.Param.ITEM_ID to EVENT_ID, FirebaseAnalytics.Param.ITEM_NAME to RATING_POPUP)
                        )
                        preferences.setLastDatetime(DateTime.nowUnixLong())
                    }
                }

                if (preferences.getNumApertures() == rating.numAperture) {
                    preferences.setNumApertures(0)
                } else {
                    preferences.setNumApertures(preferences.getNumApertures() + 1)
                }
            }
        } catch (e: Exception) {
            f(RatingControlResponse.ERROR)
        }
    }
}
