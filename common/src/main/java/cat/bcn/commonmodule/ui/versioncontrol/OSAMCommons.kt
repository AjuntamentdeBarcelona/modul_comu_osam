package cat.bcn.commonmodule.ui.versioncontrol

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat.startActivity
import cat.bcn.commonmodule.R
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
    private val VERSION_CONTROL_POPUP_ACCEPTED = "version_control_popup_accepted"
    private val VERSION_CONTROL_POPUP_CANCELLED = "version_control_popup_cancelled"
    private val RATING_POPUP = "rating_popup_showed"
    private val RATING_POPUP_ACCEPTED = "rating_popup_accepted"
    private val RATING_POPUP_CANCELLED = "rating_popup_cancelled"
    private val RATING_POPUP_LATER = "rating_popup_later"

    actual fun versionControl(
        versionCode: Int,
        language: Language,
        f: (VersionControlResponse) -> Unit
    ) {
        GlobalScope.launch {
            try {
                val version: Version =
                    remote.getVersion(context.packageName, versionCode, language, Platform.ANDROID)

                if (!version.comparisonMode.equals(NONE)) {
                    withContext(Dispatchers.Main) {
                        val dialog = AlertDialog.Builder(context)
                            .setTitle(version.title.localize(language))
                            .setMessage(version.message.localize(language))
                            .setPositiveButton(version.ok.localize(language)) { _, _ ->
                                f(VersionControlResponse.ACCEPTED)
                                val intent = Intent(Intent.ACTION_VIEW)
                                intent.data = Uri.parse(version.url)
                                startActivity(context, intent, null)

                                analytics.logVersionControlPopUp(
                                    params = mapOf(
                                        FirebaseAnalytics.Param.ITEM_ID to EVENT_ID,
                                        FirebaseAnalytics.Param.ITEM_NAME to VERSION_CONTROL_POPUP_ACCEPTED
                                    )
                                )
                            }


                        when (version.comparisonMode) {
                            FORCE -> dialog.setCancelable(false)
                            LAZY -> {
                                dialog.setNegativeButton(version.cancel.localize(language)) { _, _ ->
                                    f(VersionControlResponse.CANCELLED)
                                    analytics.logVersionControlPopUp(
                                        params = mapOf(
                                            FirebaseAnalytics.Param.ITEM_ID to EVENT_ID,
                                            FirebaseAnalytics.Param.ITEM_NAME to VERSION_CONTROL_POPUP_CANCELLED
                                        )
                                    )
                                }
                                    .setOnDismissListener { f(VersionControlResponse.DISMISSED) }
                            }
                            INFO -> {
                                // Do nothing
                            }
                        }

                        dialog.show()
                        analytics.logVersionControlPopUp(
                            params = mapOf(
                                FirebaseAnalytics.Param.ITEM_ID to EVENT_ID,
                                FirebaseAnalytics.Param.ITEM_NAME to VERSION_CONTROL_POPUP
                            )
                        )
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) { f(VersionControlResponse.ERROR) }
            }
        }
    }

    actual fun rating(
        language: Language,
        f: (RatingControlResponse) -> Unit
    ) {
        try {
            GlobalScope.launch {
                val rating = remote.getRating(context.packageName, Platform.ANDROID)

                val shouldShowRatingDialog = shouldShowRatingDialog(
                    rating = rating,
                    lastDatetime = preferences.getLastDatetime(),
                    numAperture = preferences.getNumApertures(),
                    dontShowDialog = preferences.getDontShowAgain()
                )

                if (shouldShowRatingDialog) {
                    withContext(Dispatchers.Main) {
                        val dialog = AlertDialog.Builder(context)
                            .setTitle(context.getString(R.string.dialog_rating_title, context.applicationInfo.loadLabel(context.packageManager).toString()))
                            .setMessage(rating.message.localize(language))
                            .setPositiveButton(R.string.dialog_rating_positive) { _, _ ->
                                f(RatingControlResponse.ACCEPTED)
                                val intent = Intent(Intent.ACTION_VIEW)
                                intent.data = Uri.parse("https://play.google.com/store/apps/details?id=$context.packageName")
                                startActivity(context, intent, null)

                                analytics.logRatingPopUp(
                                    params = mapOf(
                                        FirebaseAnalytics.Param.ITEM_ID to EVENT_ID,
                                        FirebaseAnalytics.Param.ITEM_NAME to RATING_POPUP_ACCEPTED
                                    )
                                )
                            }
                            .setNegativeButton(R.string.dialog_rating_negative) { _, _ ->
                                f(RatingControlResponse.CANCELLED)
                                preferences.setDontShowAgain(true)
                                analytics.logRatingPopUp(
                                    params = mapOf(
                                        FirebaseAnalytics.Param.ITEM_ID to EVENT_ID,
                                        FirebaseAnalytics.Param.ITEM_NAME to RATING_POPUP_CANCELLED
                                    )
                                )
                            }
                            .setNeutralButton(R.string.dialog_rating_neutral) { _, _ ->
                                f(RatingControlResponse.LATER)
                                preferences.setDontShowAgain(true)
                                analytics.logRatingPopUp(
                                    params = mapOf(
                                        FirebaseAnalytics.Param.ITEM_ID to EVENT_ID,
                                        FirebaseAnalytics.Param.ITEM_NAME to RATING_POPUP_LATER
                                    )
                                )
                            }
                            .setOnDismissListener { f(RatingControlResponse.DISMISSED) }
                            .create()

                        dialog.show()
                        analytics.logRatingPopUp(
                            params = mapOf(
                                FirebaseAnalytics.Param.ITEM_ID to EVENT_ID,
                                FirebaseAnalytics.Param.ITEM_NAME to RATING_POPUP
                            )
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
