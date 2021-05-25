package cat.bcn.commonmodule.ui.versioncontrol

import cat.bcn.commonmodule.analytics.Analytics
import cat.bcn.commonmodule.analytics.CommonAnalytics
import cat.bcn.commonmodule.data.datasource.local.CommonPreferences
import cat.bcn.commonmodule.data.datasource.local.Preferences
import cat.bcn.commonmodule.data.datasource.remote.CommonRemote
import cat.bcn.commonmodule.data.datasource.remote.Remote
import cat.bcn.commonmodule.data.datasource.settings.Settings
import cat.bcn.commonmodule.model.Platform
import cat.bcn.commonmodule.model.Version
import cat.bcn.commonmodule.model.localize
import cocoapods.FirebaseAnalytics.FIRAnalytics
import cocoapods.FirebaseAnalytics.FIRAnalyticsMeta
import cocoapods.FirebaseAnalytics.kFIRParameterItemID
import cocoapods.FirebaseAnalytics.kFIRParameterItemName
import com.soywiz.klock.DateTime
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import platform.StoreKit.SKStoreReviewController
import platform.UIKit.*

actual class OSAMCommons constructor(private val vc: UIViewController) {

    private val analytics: Analytics by lazy { CommonAnalytics() }
    private val remote: Remote by lazy { CommonRemote() }
    private val preferences: Preferences by lazy { CommonPreferences(Settings("default")) }
    private val EVENT_ID = "osamcommons"
    private val VERSION_CONTROL_POPUP = "version_control_popup_showed"
    private val RATING_POPUP = "rating_popup_showed"

    actual fun versionControl(
        appId: String,
        versionCode: Int,
        language: Language,
        f: (VersionControlResponse) -> Unit
    ) {
        try {
            GlobalScope.launch(Dispatchers.Main) {
                val version = remote.getVersion(
                    appId = appId,
                    versionCode = versionCode,
                    language = language,
                    platform = Platform.IOS
                )

                val alert = UIAlertController.alertControllerWithTitle(
                    title = version.title.localize(language),
                    message = version.message.localize(language),
                    preferredStyle = UIAlertControllerStyleAlert
                )

                alert.addAction(
                    UIAlertAction.actionWithTitle(
                        title = version.ok.localize(language),
                        style = UIAlertActionStyleDefault,
                        handler = { f(VersionControlResponse.ACCEPTED) }
                    )
                )

                when (version.comparisonMode) {
                    Version.ComparisonMode.FORCE -> {
                        // Do nothing, by default will be not dismissable
                    }
                    else -> {
                        alert.addAction(UIAlertAction.actionWithTitle(
                            title = version.cancel.localize(language),
                            style = UIAlertActionStyleCancel,
                            handler = { f(VersionControlResponse.CANCELLED) }
                        ))
                    }
                }

                vc.presentViewController(alert, animated = true, completion = null)

                analytics.logVersionControlPopUp(
                    params = mapOf(kFIRParameterItemID!! to EVENT_ID, kFIRParameterItemName!! to VERSION_CONTROL_POPUP)
                )

            }
        } catch (e: Exception) {
            f(VersionControlResponse.ERROR)
        }

    }

    actual fun rating(appId: String, language: Language, f: (RatingControlResponse) -> Unit) {
        try {
            GlobalScope.launch(Dispatchers.Main) {
                val rating = remote.getRating(appId, Platform.ANDROID)

                val shouldShowRatingDialog = shouldShowRatingDialog(
                    rating = rating,
                    lastDatetime = preferences.getLastDatetime(),
                    numAperture = preferences.getNumApertures()
                )

                if (shouldShowRatingDialog) {

                    SKStoreReviewController.requestReview()

                    preferences.setLastDatetime(DateTime.nowUnixLong())
                    analytics.logRatingPopUp(
                        params = mapOf(kFIRParameterItemID!! to EVENT_ID, kFIRParameterItemName!! to RATING_POPUP)
                    )
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
