package cat.bcn.commonmodule.ui.alert

import cat.bcn.commonmodule.model.Rating
import cat.bcn.commonmodule.model.Version
import cat.bcn.commonmodule.testing.Mockable
import cat.bcn.commonmodule.ui.versioncontrol.Language
import kotlinx.cinterop.ExperimentalForeignApi
import platform.Foundation.NSBundle
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
        val (alert, _) = buildVersionAlert(
            version = version,
            language = language,
            showCheckbox = false
        )
        alert.addAction(UIAlertAction.actionWithTitle(
            title = version.ok.localize(language),
            style = UIAlertActionStyleDefault,
            handler = { onPositiveClick() }
        ))
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
        val (alert, checkboxSwitch) = buildVersionAlert(
            version = version,
            language = language,
            showCheckbox = version.checkBoxDontShowAgain.isCheckBoxVisible
        )

        alert.addAction(UIAlertAction.actionWithTitle(
            title = version.ok.localize(language),
            style = UIAlertActionStyleDefault,
            handler = {
                val isChecked = checkboxSwitch?.isOn() ?: false
                onPositiveClick(isChecked)
            }
        ))

        if (onNegativeClick != null) {
            alert.addAction(UIAlertAction.actionWithTitle(
                title = version.cancel.localize(language),
                style = UIAlertActionStyleCancel,
                handler = { onNegativeClick() }
            ))
        }

        vc.presentViewController(alert, animated = true, completion = null)
        versionControlAlert = alert
    }

    @OptIn(ExperimentalForeignApi::class)
    private fun buildVersionAlert(
        version: Version,
        language: Language,
        showCheckbox: Boolean
    ): Pair<UIAlertController, UISwitch?> {
        val alert = buildAlertController()
        val contentViewController = UIViewController()
        val containerView = buildContainerView()

        val iconView = buildIconView()
        val iconContainer = buildIconContainer(iconView)
        val titleLabel = buildTitleLabel(version, language)
        val messageLabel = buildMessageLabel(version, language)
        val stack = buildContentStack(iconContainer, titleLabel, messageLabel)

        val checkboxSwitch = if (showCheckbox) {
            addCheckboxRow(stack, version, language)
        } else {
            null
        }

        setupContentLayout(
            containerView = containerView,
            stack = stack,
            iconView = iconView,
            iconContainer = iconContainer
        )

        contentViewController.view = containerView
        alert.setValue(contentViewController, forKey = "contentViewController")

        return alert to checkboxSwitch
    }

    private fun buildAlertController(): UIAlertController =
        UIAlertController.alertControllerWithTitle(
            title = null,
            message = null,
            preferredStyle = UIAlertControllerStyleAlert
        )

    private fun buildContainerView(): UIView =
        UIView().apply {
            backgroundColor = UIColor.clearColor
        }

    private fun buildIconView(): UIImageView =
        UIImageView().apply {
            contentMode = UIViewContentMode.UIViewContentModeScaleAspectFit
            image = appIconImage()
        }

    private fun buildIconContainer(iconView: UIImageView): UIView =
        UIView().apply {
            addSubview(iconView)
        }

    private fun buildTitleLabel(version: Version, language: Language): UILabel =
        UILabel().apply {
            text = version.title.localize(language)
            font = UIFont.systemFontOfSize(20.0, weight = UIFontWeightSemibold)
            textAlignment = NSTextAlignmentCenter
            textColor = UIColor.blackColor
        }

    private fun buildMessageLabel(version: Version, language: Language): UILabel =
        UILabel().apply {
            text = version.message.localize(language)
            numberOfLines = 0
            font = UIFont.systemFontOfSize(16.0)
            textAlignment = NSTextAlignmentCenter
            textColor = UIColor.blackColor
        }

    private fun buildContentStack(
        iconContainer: UIView,
        titleLabel: UILabel,
        messageLabel: UILabel
    ): UIStackView =
        UIStackView().apply {
            axis = UILayoutConstraintAxisVertical
            alignment = UIStackViewAlignmentFill
            spacing = 12.0
            addArrangedSubview(iconContainer)
            addArrangedSubview(titleLabel)
            addArrangedSubview(messageLabel)
        }

    private fun addCheckboxRow(
        stack: UIStackView,
        version: Version,
        language: Language
    ): UISwitch {
        val row = UIStackView().apply {
            axis = UILayoutConstraintAxisHorizontal
            alignment = UIStackViewAlignmentCenter
            spacing = 12.0
        }

        val checkboxLabel = UILabel().apply {
            text = version.checkBoxDontShowAgain.text.localize(language)
            font = UIFont.systemFontOfSize(16.0)
            textColor = UIColor.blackColor
        }

        val switch = UISwitch()
        row.addArrangedSubview(checkboxLabel)
        row.addArrangedSubview(UIView().apply {
            setContentHuggingPriority(1f, UILayoutConstraintAxisHorizontal)
        })
        row.addArrangedSubview(switch)

        row.translatesAutoresizingMaskIntoConstraints = false
        stack.addArrangedSubview(row)
        row.widthAnchor.constraintEqualToAnchor(stack.widthAnchor).active = true
        return switch
    }

    private fun setupContentLayout(
        containerView: UIView,
        stack: UIStackView,
        iconView: UIImageView,
        iconContainer: UIView
    ) {
        containerView.addSubview(stack)
        stack.translatesAutoresizingMaskIntoConstraints = false
        iconView.translatesAutoresizingMaskIntoConstraints = false
        iconContainer.translatesAutoresizingMaskIntoConstraints = false

        NSLayoutConstraint.activateConstraints(listOf(
            iconView.widthAnchor.constraintEqualToConstant(90.0),
            iconView.heightAnchor.constraintEqualToConstant(90.0),
            iconView.centerXAnchor.constraintEqualToAnchor(iconContainer.centerXAnchor),
            iconView.centerYAnchor.constraintEqualToAnchor(iconContainer.centerYAnchor),
            iconContainer.heightAnchor.constraintEqualToConstant(90.0),
            stack.topAnchor.constraintEqualToAnchor(containerView.topAnchor, constant = 16.0),
            stack.leadingAnchor.constraintEqualToAnchor(containerView.leadingAnchor, constant = 16.0),
            stack.trailingAnchor.constraintEqualToAnchor(containerView.trailingAnchor, constant = -16.0),
            stack.bottomAnchor.constraintEqualToAnchor(containerView.bottomAnchor, constant = -8.0)
        ))
    }

    private fun appIconImage(): UIImage? {
        UIImage.imageNamed("dialog_app_icon")?.let { return it }
        UIImage.imageNamed("ic_launcher")?.let { return it }
        return appIconFromBundleInfo()
    }

    private fun appIconFromBundleInfo(): UIImage? {
        val info = NSBundle.mainBundle.infoDictionary ?: return null
        val icons = (info as Map<Any?, Any?>)["CFBundleIcons"] as? Map<Any?, Any?> ?: return null
        val primary = icons["CFBundlePrimaryIcon"] as? Map<Any?, Any?> ?: return null
        val files = primary["CFBundleIconFiles"] as? List<*> ?: return null
        val iconName = files.lastOrNull() as? String ?: return null
        return UIImage.imageNamed(iconName)
    }
}
