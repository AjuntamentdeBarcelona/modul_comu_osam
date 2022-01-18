package cat.bcn.commonmodule.ui.versioncontrol

import cat.bcn.commonmodule.analytics.Analytics
import cat.bcn.commonmodule.analytics.CommonAnalytics
import cat.bcn.commonmodule.data.datasource.local.CommonPreferences
import cat.bcn.commonmodule.data.datasource.local.Preferences
import cat.bcn.commonmodule.data.datasource.remote.CommonRemote
import cat.bcn.commonmodule.data.datasource.remote.Remote
import cat.bcn.commonmodule.data.datasource.settings.Settings
import cat.bcn.commonmodule.model.*
import cocoapods.FirebaseAnalytics.kFIRParameterItemID
import cocoapods.FirebaseAnalytics.kFIRParameterItemName
import cocoapods.FirebaseCrashlytics.FIRCrashlytics
import cocoapods.FirebaseCrashlytics.FIRExceptionModel
import com.soywiz.klock.DateTime
import io.ktor.client.engine.ios.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import platform.Foundation.NSBundle
import platform.Foundation.NSURL
import platform.StoreKit.SKStoreReviewController
import platform.UIKit.*
import platform.darwin.nil

actual class OSAMCommons constructor(
    private val vc: UIViewController,
    private val backendEndpoint: String,
) {

    private val analytics: Analytics by lazy { CommonAnalytics() }
    private val remote: Remote by lazy { CommonRemote(backendEndpoint) }
    private val preferences: Preferences by lazy { CommonPreferences(Settings("default")) }
    private val EVENT_ID = "osamcommons"
    private val VERSION_CONTROL_POPUP = "version_control_popup_showed"
    private val VERSION_CONTROL_POPUP_ACCEPTED = "version_control_popup_accepted"
    private val VERSION_CONTROL_POPUP_CANCELLED = "version_control_popup_cancelled"
    private val RATING_POPUP = "rating_popup_showed"

    actual fun versionControl(
        language: Language,
        f: (VersionControlResponse) -> Unit
    ) {
        GlobalScope.launch(Dispatchers.Main) {
            try {
                val buildVersion = NSBundle.mainBundle.infoDictionary!!["CFBundleVersion"] as String
                var version = Version(
                    id = 0,
                    appId = 0,
                    packageName = NSBundle.mainBundle.bundleIdentifier!!,
                    versionCode = buildVersion.toLong(),
                    versionName = NSBundle.mainBundle.infoDictionary!!["CFBundleName"] as String,
                    platform = Platform.IOS,
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
                    if (isOnline()) {
                        version = remote.getVersion(
                            appId = NSBundle.mainBundle.bundleIdentifier!!,
                            versionCode = buildVersion.toInt(),
                            language = language,
                            platform = Platform.IOS
                        )
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
                } catch (e: IosHttpRequestException) {
                    println("Device not has connection")
                } catch (e: Exception) {
                    val exception = FIRExceptionModel(name = e::class.qualifiedName!!, reason = e.stackTraceToString())
                    FIRCrashlytics.crashlytics().recordExceptionModel(exception)
                }

                if (!version.comparisonMode.equals(Version.ComparisonMode.NONE)) {
                    val alert = UIAlertController.alertControllerWithTitle(
                        title = version.title.localize(language),
                        message = version.message.localize(language),
                        preferredStyle = UIAlertControllerStyleAlert
                    )

                    when (version.comparisonMode) {
                        Version.ComparisonMode.FORCE -> {
                            alert.addAction(
                                UIAlertAction.actionWithTitle(
                                    title = version.ok.localize(language),
                                    style = UIAlertActionStyleDefault,
                                    handler = {
                                        UIApplication.sharedApplication.openURL(NSURL(string = version.url))
                                        f(VersionControlResponse.ACCEPTED)
                                        analytics.logVersionControlPopUp(
                                            params = mapOf(
                                                kFIRParameterItemID!! to EVENT_ID,
                                                kFIRParameterItemName!! to VERSION_CONTROL_POPUP_ACCEPTED
                                            )
                                        )
                                    }
                                )
                            )
                        }
                        Version.ComparisonMode.LAZY -> {
                            alert.addAction(
                                UIAlertAction.actionWithTitle(
                                    title = version.ok.localize(language),
                                    style = UIAlertActionStyleDefault,
                                    handler = {
                                        UIApplication.sharedApplication.openURL(NSURL(string = version.url))
                                        f(VersionControlResponse.ACCEPTED)
                                        analytics.logVersionControlPopUp(
                                            params = mapOf(
                                                kFIRParameterItemID!! to EVENT_ID,
                                                kFIRParameterItemName!! to VERSION_CONTROL_POPUP_ACCEPTED
                                            )
                                        )
                                    }
                                )
                            )
                            alert.addAction(UIAlertAction.actionWithTitle(
                                title = version.cancel.localize(language),
                                style = UIAlertActionStyleCancel,
                                handler = {
                                    f(VersionControlResponse.CANCELLED)
                                    analytics.logVersionControlPopUp(
                                        params = mapOf(
                                            kFIRParameterItemID!! to EVENT_ID,
                                            kFIRParameterItemName!! to VERSION_CONTROL_POPUP_CANCELLED
                                        )
                                    )
                                }
                            ))
                            alert.dismissViewControllerAnimated(true, null)
                        }
                        Version.ComparisonMode.INFO -> {
                            alert.addAction(
                                UIAlertAction.actionWithTitle(
                                    title = version.ok.localize(language),
                                    style = UIAlertActionStyleDefault,
                                    handler = {
                                        f(VersionControlResponse.ACCEPTED)
                                        analytics.logVersionControlPopUp(
                                            params = mapOf(
                                                kFIRParameterItemID!! to EVENT_ID,
                                                kFIRParameterItemName!! to VERSION_CONTROL_POPUP_ACCEPTED
                                            )
                                        )
                                    }
                                )
                            )
                        }
                    }

                    vc.presentViewController(alert, animated = true, completion = null)
                    analytics.logVersionControlPopUp(
                        params = mapOf(
                            kFIRParameterItemID!! to EVENT_ID,
                            kFIRParameterItemName!! to VERSION_CONTROL_POPUP
                        )
                    )
                } else {
                    f(VersionControlResponse.DISMISSED)
                }
            } catch (e: Exception) {
                f(VersionControlResponse.ERROR)
                val exception = FIRExceptionModel(name = e::class.qualifiedName!!, reason = e.stackTraceToString())
                FIRCrashlytics.crashlytics().recordExceptionModel(exception)
            }
        }
    }

    actual fun rating(language: Language, f: (RatingControlResponse) -> Unit) {
        GlobalScope.launch(Dispatchers.Main) {
            try {
                var rating: Rating = Rating(
                    id = 0,
                    appId = 0,
                    packageName = NSBundle.mainBundle.bundleIdentifier!!,
                    platform = Platform.IOS,
                    minutes = preferences.getRatingDateInterval(),
                    numAperture = preferences.getRatingNumApertures(),
                    message = Text(
                            es = preferences.getRatingControlMessageEs(),
                            en = preferences.getRatingControlMessageEn(),
                            ca = preferences.getRatingControlMessageCa()
                    )
                )

                try {
                    if (isOnline()) {
                        rating = remote.getRating(NSBundle.mainBundle.bundleIdentifier!!, Platform.IOS)
                        preferences.setRatingNumApertures(rating.numAperture)
                        preferences.setRatingDateInterval(rating.minutes)
                    }
                } catch (e: IosHttpRequestException) {
                    println("Device not has connection")
                } catch (e: Exception) {
                    val exception = FIRExceptionModel(name = e::class.qualifiedName!!, reason = e.stackTraceToString())
                    FIRCrashlytics.crashlytics().recordExceptionModel(exception)
                }

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
                    SKStoreReviewController.requestReview()
                    f(RatingControlResponse.ACCEPTED)
                    preferences.setLastDatetime(DateTime.nowUnixLong())
                    analytics.logRatingPopUp(
                        params = mapOf(kFIRParameterItemID!! to EVENT_ID, kFIRParameterItemName!! to RATING_POPUP)
                    )
                } else {
                    f(RatingControlResponse.DISMISSED)
                }

                if (preferences.getNumApertures() == rating.numAperture) {
                    preferences.setNumApertures(0)
                } else {
                    preferences.setNumApertures(preferences.getNumApertures() + 1)
                }
            } catch (e: Exception) {
                f(RatingControlResponse.ERROR)
                val exception = FIRExceptionModel(name = e::class.qualifiedName!!, reason = e.stackTraceToString())
                FIRCrashlytics.crashlytics().recordExceptionModel(exception)
            }
        }
    }

    private fun isOnline(): Boolean {
        //Para futuro. Es complicado meterlo aqui. La opcion seria hacer un PING
        //Por ahora, si no hay conexion entra en el catch y sigue la ejecucion como si estuviera offline
        return true
    }
}
