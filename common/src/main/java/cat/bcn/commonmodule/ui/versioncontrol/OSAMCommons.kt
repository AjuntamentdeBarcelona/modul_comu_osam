package cat.bcn.commonmodule.ui.versioncontrol

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.Uri
import android.os.Build
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
import cat.bcn.commonmodule.model.*
import cat.bcn.commonmodule.model.Version.ComparisonMode.*
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.soywiz.klock.DateTime
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

actual class OSAMCommons constructor(
    private val context: Context,
    private val backendEndpoint: String,
) {

    private val analytics: Analytics by lazy { CommonAnalytics(context) }
    private val remote: Remote by lazy { CommonRemote(backendEndpoint) }
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
        language: Language,
        f: (VersionControlResponse) -> Unit
    ) {
        GlobalScope.launch {
            try {
                var version = Version(
                    id = 0,
                    appId = 0,
                    packageName = context.packageName,
                    versionCode = context.packageManager.getPackageInfo(context.packageName, 0).versionCode.toLong(),
                    versionName = context.packageManager.getPackageInfo(context.packageName, 0).versionName,
                    platform = Platform.ANDROID,
                    comparisonMode = preferences.getVersionControlComparisionMode(),
                    title = Text(
                        es = preferences.getVersionControlTitleEs(),
                        en = preferences.getVersionControlTitleEn(),
                        ca = preferences.getVersionControlTitleCa()
                    ),
                    message = Text(
                        es = preferences.getVersionControlMessageEs(),
                        en = preferences.getVersionControlMessageEn(),
                        ca = preferences.getVersionControlMessageCa()
                    ),
                    ok = Text(
                        es = preferences.getVersionControlOkEs(),
                        en = preferences.getVersionControlOkEn(),
                        ca = preferences.getVersionControlOkCa()
                    ),
                    cancel = Text(
                        es = preferences.getVersionControlCancelEs(),
                        en = preferences.getVersionControlCancelEn(),
                        ca = preferences.getVersionControlCancelCa(),
                    ),
                    url = preferences.getVersionControlUrl()
                )

                try {
                    if (isOnline(context)) {
                        version = remote.getVersion(context.packageName, context.packageManager.getPackageInfo(context.packageName, 0).versionCode, language, Platform.ANDROID)
                        preferences.setVersionControlTitleEs(version.title.localize(Language.ES))
                        preferences.setVersionControlTitleEn(version.title.localize(Language.EN))
                        preferences.setVersionControlTitleCa(version.title.localize(Language.CA))
                        preferences.setVersionControlMessageEs(version.message.localize(Language.ES))
                        preferences.setVersionControlMessageEn(version.message.localize(Language.EN))
                        preferences.setVersionControlMessageCa(version.message.localize(Language.CA))
                        preferences.setVersionControlOkEs(version.ok.localize(Language.ES))
                        preferences.setVersionControlOkEn(version.ok.localize(Language.EN))
                        preferences.setVersionControlOkCa(version.ok.localize(Language.CA))
                        preferences.setVersionControlCancelEs(version.cancel.localize(Language.ES))
                        preferences.setVersionControlCancelEn(version.cancel.localize(Language.EN))
                        preferences.setVersionControlCancelCa(version.cancel.localize(Language.CA))
                        preferences.setVersionControlUrl(version.url)
                        preferences.setVersionControlComparisionMode(version.comparisonMode)
                    }
                } catch (e: Exception) {
                    FirebaseCrashlytics.getInstance().recordException(e)
                }

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
                                dialog.setPositiveButton(version.ok.localize(language)) { _, _ ->
                                    f(VersionControlResponse.ACCEPTED)
                                    analytics.logVersionControlPopUp(
                                        params = mapOf(
                                            FirebaseAnalytics.Param.ITEM_ID to EVENT_ID,
                                            FirebaseAnalytics.Param.ITEM_NAME to VERSION_CONTROL_POPUP_ACCEPTED
                                        )
                                    )
                                }
                                    .setOnDismissListener { f(VersionControlResponse.DISMISSED) }
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
                } else {
                    withContext(Dispatchers.Main) { f(VersionControlResponse.DISMISSED) }
                }
            } catch (e: Exception) {
                FirebaseCrashlytics.getInstance().recordException(e)
                withContext(Dispatchers.Main) { f(VersionControlResponse.ERROR) }
            }
        }
    }

    actual fun rating(
        language: Language,
        f: (RatingControlResponse) -> Unit
    ) {
        GlobalScope.launch {
            try {
                var rating: Rating = Rating(
                    id = 0,
                    appId = 0,
                    packageName = context.packageName,
                    platform = Platform.ANDROID,
                    minutes = preferences.getRatingDateInterval(),
                    numAperture = preferences.getRatingNumApertures(),
                    message = Text(
                        es = preferences.getRatingControlMessageEs(),
                        en = preferences.getRatingControlMessageEn(),
                        ca = preferences.getRatingControlMessageCa()
                    )
                )
                try {
                    if (isOnline(context)) {
                        rating = remote.getRating(context.packageName, Platform.ANDROID)
                        preferences.setRatingNumApertures(rating.numAperture)
                        preferences.setRatingDateInterval(rating.minutes)
                        preferences.setRatingControlMessageEs(rating.message.localize(Language.ES))
                        preferences.setRatingControlMessageEn(rating.message.localize(Language.EN))
                        preferences.setRatingControlMessageCa(rating.message.localize(Language.CA))
                    }
                } catch (e: Exception) {
                    FirebaseCrashlytics.getInstance().recordException(e)
                }

                //Initialize LastDateTime if needed
                if (preferences.getLastDatetime() == 0L) {
                    preferences.setLastDatetime(DateTime.nowUnixLong())
                }

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
                        preferences.setLastDatetime(DateTime.nowUnixLong())
                        analytics.logRatingPopUp(
                            params = mapOf(
                                FirebaseAnalytics.Param.ITEM_ID to EVENT_ID,
                                FirebaseAnalytics.Param.ITEM_NAME to RATING_POPUP
                            )
                        )
                    }
                } else {
                    withContext(Dispatchers.Main) {(RatingControlResponse.LATER)}
                }

                if (preferences.getNumApertures() == rating.numAperture) {
                    preferences.setNumApertures(0)
                } else {
                    preferences.setNumApertures(preferences.getNumApertures() + 1)
                }
            } catch (e: Exception) {
                FirebaseCrashlytics.getInstance().recordException(e)
                withContext(Dispatchers.Main) { f(RatingControlResponse.ERROR) }
            }
        }
    }

    private fun isOnline(context: Context): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val capabilities = connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
            if (capabilities != null) {
                when {
                    capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> {
                        return true
                    }
                    capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> {
                        return true
                    }
                    capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> {
                        return true
                    }
                }
            }
        } else {
            val activeNetworkInfo = connectivityManager.activeNetworkInfo
            if (activeNetworkInfo != null && activeNetworkInfo.isConnected) {
                return true
            }
        }
        return false
    }
}
