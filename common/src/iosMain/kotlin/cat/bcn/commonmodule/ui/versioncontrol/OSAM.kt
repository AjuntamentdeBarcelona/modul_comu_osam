package cat.bcn.commonmodule.ui.versioncontrol

import cat.bcn.commonmodule.data.datasource.local.CommonPreferences
import cat.bcn.commonmodule.data.datasource.local.Preferences
import cat.bcn.commonmodule.data.datasource.remote.CommonRemote
import cat.bcn.commonmodule.data.datasource.remote.Remote
import cat.bcn.commonmodule.data.datasource.settings.Settings
import cat.bcn.commonmodule.model.Platform
import cat.bcn.commonmodule.model.Version
import cat.bcn.commonmodule.model.localize
import com.soywiz.klock.DateTime
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import platform.UIKit.*

actual class OSAM constructor(private val vc: UIViewController) {

    private val remote: Remote by lazy { CommonRemote() }
    private val preferences: Preferences by lazy { CommonPreferences(Settings("default")) }

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

                    val alert = UIAlertController.alertControllerWithTitle(
                        title = "titulo",
                        message = rating.message.localize(language),
                        preferredStyle = UIAlertControllerStyleAlert
                    )

                    alert.addAction(
                        UIAlertAction.actionWithTitle(
                            title = "Aceptar",
                            style = UIAlertActionStyleDefault,
                            handler = { f(RatingControlResponse.ACCEPTED) }
                        )
                    )

                    alert.addAction(UIAlertAction.actionWithTitle(
                        title = "Cancelar",
                        style = UIAlertActionStyleCancel,
                        handler = { f(RatingControlResponse.CANCELLED) }
                    ))

                    vc.presentViewController(alert, animated = true, completion = null)

                    preferences.setLastDatetime(DateTime.nowUnixLong())
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
