package cat.bcn.commonmodule.ui.alert

import cat.bcn.commonmodule.model.Rating
import cat.bcn.commonmodule.model.Version
import cat.bcn.commonmodule.testing.Mockable
import cat.bcn.commonmodule.ui.versioncontrol.Language
import kotlinx.cinterop.ExperimentalForeignApi
import platform.Foundation.setValue
import platform.StoreKit.SKStoreReviewController
import platform.UIKit.*

@Mockable
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
        onPositiveClick: (isCheckboxChecked: Boolean) -> Unit,
        onNegativeClick: () -> Unit,
        onDismissClick: () -> Unit
    ) {
        showConfigurableVersionAlert(
            version = version,
            language = language,
            onPositiveClick = onPositiveClick,
            onNegativeClick = onNegativeClick,
            onDismiss = onDismissClick
        )
    }

    actual fun showVersionControlInfo(
        version: Version,
        language: Language,
        onPositiveClick: (isCheckboxChecked: Boolean) -> Unit,
        onDismissClick: () -> Unit
    ) {
        showConfigurableVersionAlert(
            version = version,
            language = language,
            onPositiveClick = onPositiveClick,
            onNegativeClick = null, // No "Cancel" button for info-style alerts
            onDismiss = onDismissClick
        )
    }

    actual fun showRating(
        rating: Rating,
        language: Language,
        onRatingPopupShown: () -> Unit,
        onRatingPopupError: () -> Unit
    ) {
        SKStoreReviewController.requestReview()
        onRatingPopupShown()
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

    /**
     * Builds and displays a configurable version alert.
     *
     * This function handles the core logic for all alert types that may include a checkbox.
     * It conditionally adds a custom content view for the checkbox or sets a simple message.
     */
    @OptIn(ExperimentalForeignApi::class)
    private fun showConfigurableVersionAlert(
        version: Version,
        language: Language,
        onPositiveClick: (isCheckboxChecked: Boolean) -> Unit,
        onNegativeClick: (() -> Unit)?,
        onDismiss: () -> Unit
    ) {
        val alert = UIAlertController.alertControllerWithTitle(
            title = version.title.localize(language),
            message = null, // Message is handled conditionally below
            preferredStyle = UIAlertControllerStyleAlert
        )

        var checkboxSwitch: UISwitch? = null

        if (version.checkBoxDontShowAgain.isCheckBoxVisible) {
            // Create a custom UIViewController to host our message and switch.
            val contentViewController = UIViewController()
            val containerView = UIView()

            // CORRECTED: Use parameterless constructors when using Auto Layout.
            val messageLabel = UILabel().apply {
                text = version.message.localize(language)
                numberOfLines = 0
                font = UIFont.systemFontOfSize(13.0)
                textAlignment = NSTextAlignmentCenter
            }

            // CORRECTED: Use parameterless constructors when using Auto Layout.
            val switch = UISwitch()
            checkboxSwitch = switch // Keep a reference to read its state later

            // CORRECTED: Use parameterless constructors when using Auto Layout.
            val checkboxLabel = UILabel().apply {
                text = version.checkBoxDontShowAgain.text.localize(language)
                font = UIFont.systemFontOfSize(13.0)
            }

            // Add subviews and configure Auto Layout
            containerView.addSubview(messageLabel)
            containerView.addSubview(switch)
            containerView.addSubview(checkboxLabel)

            messageLabel.translatesAutoresizingMaskIntoConstraints = false
            switch.translatesAutoresizingMaskIntoConstraints = false
            checkboxLabel.translatesAutoresizingMaskIntoConstraints = false

            NSLayoutConstraint.activateConstraints(listOf(
                messageLabel.topAnchor.constraintEqualToAnchor(containerView.topAnchor, constant = 16.0),
                messageLabel.leadingAnchor.constraintEqualToAnchor(containerView.leadingAnchor, constant = 16.0),
                messageLabel.trailingAnchor.constraintEqualToAnchor(containerView.trailingAnchor, constant = -16.0),

                switch.topAnchor.constraintEqualToAnchor(messageLabel.bottomAnchor, constant = 16.0),
                switch.leadingAnchor.constraintEqualToAnchor(containerView.leadingAnchor, constant = 16.0),
                switch.bottomAnchor.constraintEqualToAnchor(containerView.bottomAnchor, constant = -16.0),

                checkboxLabel.leadingAnchor.constraintEqualToAnchor(switch.trailingAnchor, constant = 8.0),
                checkboxLabel.trailingAnchor.constraintEqualToAnchor(containerView.trailingAnchor, constant = -16.0),
                checkboxLabel.centerYAnchor.constraintEqualToAnchor(switch.centerYAnchor)
            ))

            contentViewController.view = containerView
            // Embed the custom view controller into the alert.
            alert.setValue(contentViewController, forKey = "contentViewController")
        } else {
            // If no checkbox is needed, just set the standard message.
            alert.message = version.message.localize(language)
        }

        // Add the "OK" button. Its handler reads the switch state.
        alert.addAction(UIAlertAction.actionWithTitle(
            title = version.ok.localize(language),
            style = UIAlertActionStyleDefault,
            handler = {
                val isChecked = checkboxSwitch?.isOn() ?: false
                onPositiveClick(isChecked)
            }
        ))

        // Conditionally add the "Cancel" button.
        if (onNegativeClick != null) {
            alert.addAction(UIAlertAction.actionWithTitle(
                title = version.cancel.localize(language),
                style = UIAlertActionStyleCancel,
                handler = {
                    onNegativeClick()
                }
            ))
        }

        vc.presentViewController(alert, animated = true, completion = null)
        versionControlAlert = alert
    }
}

