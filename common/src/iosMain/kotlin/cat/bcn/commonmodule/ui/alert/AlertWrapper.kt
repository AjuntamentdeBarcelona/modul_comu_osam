package cat.bcn.commonmodule.ui.alert

import cat.bcn.commonmodule.model.Rating
import cat.bcn.commonmodule.model.Version
import cat.bcn.commonmodule.ui.versioncontrol.Language
import platform.StoreKit.SKStoreReviewController
import platform.UIKit.*

internal actual class AlertWrapper(private val vc: UIViewController) {

    private var versionControlAlert: UIAlertController? = null

    actual fun showVersionControlForce(
        version: Version,
        language: Language,
        onPositiveClick: () -> Unit
    ) {
        val alert = buildCommonVersionAlert(version, language, onPositiveClick)
        vc.presentViewController(alert, animated = true, completion = null)
        versionControlAlert = alert
    }

    actual fun showVersionControlLazy(
        version: Version,
        language: Language,
        onPositiveClick: () -> Unit,
        onNegativeClick: () -> Unit,
        onDismissClick: () -> Unit
    ) {
        val alert = buildCommonVersionAlert(version, language, onPositiveClick)
        alert.addAction(UIAlertAction.actionWithTitle(
            title = version.cancel.localize(language),
            style = UIAlertActionStyleCancel,
            handler = { onNegativeClick() }
        ))
        vc.presentViewController(alert, animated = true, completion = null)
        versionControlAlert = alert
    }

    actual fun showVersionControlInfo(
        version: Version,
        language: Language,
        onPositiveClick: () -> Unit,
        onDismissClick: () -> Unit
    ) {
        val alert = buildCommonVersionAlert(version, language, onPositiveClick)
        vc.presentViewController(alert, animated = true, completion = null)
        versionControlAlert = alert
    }

    actual fun showRating(
        rating: Rating,
        language: Language,
        onPositiveClick: () -> Unit,
        onNegativeClick: () -> Unit,
        onNeutralClick: () -> Unit,
        onDismissClick: () -> Unit
    ) {
        SKStoreReviewController.requestReview()
        onPositiveClick()
    }

    actual fun isVersionControlShowing(): Boolean =
        versionControlAlert?.let { vc.presentedViewController == it } ?: false

    actual fun isRatingShowing(): Boolean = false //iOS controls the rating alert

    private fun buildCommonVersionAlert(
        version: Version,
        language: Language,
        onPositiveClick: () -> Unit
    ): UIAlertController =
        UIAlertController.alertControllerWithTitle(
            title = version.title.localize(language),
            message = version.message.localize(language),
            preferredStyle = UIAlertControllerStyleAlert
        ).apply {
            addAction(UIAlertAction.actionWithTitle(
                title = version.ok.localize(language),
                style = UIAlertActionStyleDefault,
                handler = { onPositiveClick() }
            ))
        }
}