@file:OptIn(ExperimentalForeignApi::class, BetaInteropApi::class)
package cat.bcn.commonmodule.ui.alert

import cat.bcn.commonmodule.model.Rating
import cat.bcn.commonmodule.model.Version
import cat.bcn.commonmodule.testing.Mockable
import cat.bcn.commonmodule.ui.versioncontrol.Language
import cat.bcn.commonmodule.ui.utils.AccessibilityKeyNavigationManager
import kotlinx.cinterop.ExperimentalForeignApi
import platform.Foundation.NSBundle
import platform.UIKit.*
import platform.StoreKit.SKStoreReviewController
import platform.darwin.NSObject
import platform.objc.sel_registerName
import kotlinx.cinterop.*

class AccessibleAlertController : UIViewController(nibName = null, bundle = null) {
    var navManager: AccessibilityKeyNavigationManager? = null
    var containerView: UIView? = null

    override fun canBecomeFirstResponder(): Boolean = true

    override fun viewDidLoad() {
        super.viewDidLoad()
        view.backgroundColor = UIColor.blackColor.colorWithAlphaComponent(0.4)
        setupKeyCommands()
    }

    override fun viewDidAppear(animated: Boolean) {
        super.viewDidAppear(animated)
        becomeFirstResponder()
    }

    override fun viewWillDisappear(animated: Boolean) {
        super.viewWillDisappear(animated)
        navManager?.clearHighlight()
    }

    @ObjCAction
    fun handleKeyCommand(command: UIKeyCommand) {
        navManager?.handleKeyCommand(command)
    }
    
    private fun setupKeyCommands() {
        val commands = listOf(
            UIKeyCommand.keyCommandWithInput("\t", 0L, sel_registerName("handleKeyCommand:")),
            UIKeyCommand.keyCommandWithInput(UIKeyInputRightArrow, 0L, sel_registerName("handleKeyCommand:")),
            UIKeyCommand.keyCommandWithInput(UIKeyInputDownArrow, 0L, sel_registerName("handleKeyCommand:")),
            UIKeyCommand.keyCommandWithInput(UIKeyInputLeftArrow, 0L, sel_registerName("handleKeyCommand:")),
            UIKeyCommand.keyCommandWithInput(UIKeyInputUpArrow, 0L, sel_registerName("handleKeyCommand:")),
            UIKeyCommand.keyCommandWithInput("\r", 0L, sel_registerName("handleKeyCommand:")),
            UIKeyCommand.keyCommandWithInput(" ", 0L, sel_registerName("handleKeyCommand:"))
        )
        commands.forEach { addKeyCommand(it) }
    }
}

// Separate target object to handle Objective-C actions
private class AlertTarget(private val wrapper: AlertWrapper) : NSObject() {
    @ObjCAction
    fun onPositiveClickAction() {
        wrapper.onPositiveClickAction()
    }

    @ObjCAction
    fun onNegativeClickAction() {
        wrapper.onNegativeClickAction()
    }

    @ObjCAction
    fun onDismissAction() {
        wrapper.onDismissAction()
    }
}

private data class AlertBuildResult(
    val alert: AccessibleAlertController,
    val checkboxSwitch: UISwitch?,
    val keyboardViews: List<UIView>
)

@Mockable
internal actual class AlertWrapper(private val vc: UIViewController) {

    private var versionControlAlert: AccessibleAlertController? = null
    private var checkboxSwitch: UISwitch? = null
    private var onPositiveClick: (() -> Unit)? = null
    private var onPositiveClickWithCheckbox: ((Boolean) -> Unit)? = null
    private var onNegativeClickWithCheckbox: ((Boolean) -> Unit)? = null
    private var onDismiss: (() -> Unit)? = null
    
    private val target = AlertTarget(this)

    private val VERY_DARK_GREY = UIColor.colorWithRed(28.0/255.0, 28.0/255.0, 28.0/255.0, 1.0)
    private val MEDIUM_LIGHT_GREY = UIColor.colorWithRed(176.0/255.0, 176.0/255.0, 176.0/255.0, 1.0)

    actual fun showVersionControlForce(
        version: Version,
        language: Language,
        isDarkMode: Boolean,
        applyComModStyles: Boolean,
        onPositiveClick: () -> Unit
    ) {
        this.onPositiveClick = onPositiveClick
        this.onNegativeClickWithCheckbox = null
        this.onDismiss = null

        val result = buildVersionAlert(
            version = version,
            language = language,
            showCheckbox = false,
            showNegative = false,
            showClose = false,
            isDarkMode = isDarkMode,
            applyComModStyles = applyComModStyles
        )

        presentAlert(result)
    }

    actual fun showVersionControlLazy(
        version: Version,
        language: Language,
        isDarkMode: Boolean,
        applyComModStyles: Boolean,
        onPositiveClick: (isCheckboxChecked: Boolean) -> Unit,
        onNegativeClick: (isCheckboxChecked: Boolean) -> Unit,
        onDismissClick: () -> Unit
    ) {
        this.onPositiveClickWithCheckbox = onPositiveClick
        this.onNegativeClickWithCheckbox = onNegativeClick
        this.onDismiss = onDismissClick

        val result = buildVersionAlert(
            version = version,
            language = language,
            showCheckbox = version.checkBoxDontShowAgain.isCheckBoxVisible,
            showNegative = true,
            showClose = true,
            isDarkMode = isDarkMode,
            applyComModStyles = applyComModStyles
        )

        presentAlert(result)
    }

    actual fun showVersionControlInfo(
        version: Version,
        language: Language,
        isDarkMode: Boolean,
        applyComModStyles: Boolean,
        onPositiveClick: (isCheckboxChecked: Boolean) -> Unit,
        onDismissClick: () -> Unit
    ) {
        this.onPositiveClickWithCheckbox = onPositiveClick
        this.onNegativeClickWithCheckbox = null
        this.onDismiss = onDismissClick

        val result = buildVersionAlert(
            version = version,
            language = language,
            showCheckbox = version.checkBoxDontShowAgain.isCheckBoxVisible,
            showNegative = false,
            showClose = true,
            isDarkMode = isDarkMode,
            applyComModStyles = applyComModStyles
        )

        presentAlert(result)
    }

    private fun presentAlert(result: AlertBuildResult) {
        val alert = result.alert
        alert.modalPresentationStyle = UIModalPresentationOverFullScreen
        alert.modalTransitionStyle = UIModalTransitionStyleCrossDissolve
        
        versionControlAlert = alert
        
        val navManager = AccessibilityKeyNavigationManager(result.keyboardViews)
        alert.navManager = navManager
        
        val topVC = topViewController(vc)
        println("OSAMCommons - AlertWrapper: Presenting alert on topVC: $topVC")
        topVC.presentViewController(alert, animated = true, completion = null)
    }

    private fun topViewController(base: UIViewController?): UIViewController {
        val presented = base?.presentedViewController
        if (presented != null) {
            return topViewController(presented)
        }
        if (base is UINavigationController) {
            return topViewController(base.visibleViewController)
        }
        if (base is UITabBarController) {
            return topViewController(base.selectedViewController)
        }
        return base ?: vc
    }

    actual fun showRating(
        rating: Rating,
        language: Language,
        isDarkMode: Boolean,
        applyComModStyles: Boolean,
        onRatingPopupShown: () -> Unit,
        onRatingPopupError: () -> Unit
    ) {
        SKStoreReviewController.requestReview()
        onRatingPopupShown()
    }

    actual fun isVersionControlShowing(): Boolean {
        val showing = versionControlAlert?.presentingViewController != null
        println("OSAMCommons - AlertWrapper: isVersionControlShowing=$showing (based on presentingViewController)")
        return showing
    }

    actual fun isRatingShowing(): Boolean = false

    private fun buildVersionAlert(
        version: Version,
        language: Language,
        showCheckbox: Boolean,
        showNegative: Boolean,
        showClose: Boolean,
        isDarkMode: Boolean,
        applyComModStyles: Boolean
    ): AlertBuildResult {
        val alert = AccessibleAlertController()
        val containerView = buildContainerView(isDarkMode, applyComModStyles)
        val keyboardViews = mutableListOf<UIView>()

        val closeButton = if (showClose) {
            buildCloseButton(isDarkMode, applyComModStyles).also {
                containerView.addSubview(it)
                keyboardViews.add(it)
            }
        } else null

        val iconView = buildIconView()
        val iconContainer = if (iconView.image != null) buildIconContainer(iconView) else null
        val titleLabel = buildTitleLabel(version, language, isDarkMode, applyComModStyles)
        val messageLabel = buildMessageLabel(version, language, isDarkMode, applyComModStyles)
        val stack = buildContentStack(iconContainer, titleLabel, messageLabel)

        val checkboxViews = if (showCheckbox) {
            addCheckboxRow(stack, version, language, isDarkMode, applyComModStyles).also {
                this.checkboxSwitch = it.first
                keyboardViews.add(it.first)
            }
        } else {
            this.checkboxSwitch = null
            null
        }

        val primaryButton = buildPrimaryButton(version, language, isDarkMode, applyComModStyles).also {
            stack.addArrangedSubview(it)
            keyboardViews.add(it)
        }
        
        val secondaryButton = if (showNegative) {
            buildSecondaryButton(version, language, isDarkMode, applyComModStyles).also {
                stack.addArrangedSubview(it)
                keyboardViews.add(it)
            }
        } else null

        setupContentLayout(
            containerView = containerView,
            stack = stack,
            iconView = iconView,
            iconContainer = iconContainer,
            closeButton = closeButton
        )
        
        setupAccessibility(
            containerView = containerView,
            iconView = iconView,
            titleLabel = titleLabel,
            messageLabel = messageLabel,
            checkboxLabel = checkboxViews?.second,
            checkboxSwitch = checkboxSwitch,
            primaryButton = primaryButton,
            secondaryButton = secondaryButton,
            closeButton = closeButton
        )

        alert.view.addSubview(containerView)
        alert.containerView = containerView
        
        containerView.translatesAutoresizingMaskIntoConstraints = false
        NSLayoutConstraint.activateConstraints(listOf(
            containerView.centerXAnchor.constraintEqualToAnchor(alert.view.centerXAnchor),
            containerView.centerYAnchor.constraintEqualToAnchor(alert.view.centerYAnchor),
            containerView.leadingAnchor.constraintGreaterThanOrEqualToAnchor(alert.view.leadingAnchor, constant = 24.0),
            containerView.trailingAnchor.constraintLessThanOrEqualToAnchor(alert.view.trailingAnchor, constant = -24.0),
            containerView.widthAnchor.constraintEqualToConstant(300.0) // Typical alert width
        ))

        return AlertBuildResult(alert, checkboxSwitch, keyboardViews)
    }

    private fun buildContainerView(isDarkMode: Boolean, applyComModStyles: Boolean): UIView =
        UIView().apply {
            if (applyComModStyles) {
                backgroundColor = if (isDarkMode) VERY_DARK_GREY else UIColor.whiteColor
            } else {
                 backgroundColor = if (isDarkMode) UIColor.blackColor else UIColor.whiteColor
            }
            layer.cornerRadius = 20.0
            clipsToBounds = true
        }

    private fun buildCloseButton(isDarkMode: Boolean, applyComModStyles: Boolean): UIButton =
        UIButton().apply {
            val config = UIImageSymbolConfiguration.configurationWithPointSize(20.0, UIImageSymbolWeightMedium)
            val image = UIImage.systemImageNamed("xmark", withConfiguration = config) ?: UIImage.imageNamed("close_mark")
            setImage(image, forState = UIControlStateNormal)
            if (applyComModStyles) {
                tintColor = if (isDarkMode) UIColor.whiteColor else UIColor.blackColor
            }
            addTarget(target, action = sel_registerName("onDismissAction"), forControlEvents = UIControlEventTouchUpInside.toULong())
            isAccessibilityElement = true
            accessibilityLabel = "Close"
        }

    private fun buildIconView(): UIImageView =
        UIImageView().apply {
            contentMode = UIViewContentMode.UIViewContentModeScaleAspectFit
            image = appIconImage()
            isAccessibilityElement = false
        }

    private fun buildIconContainer(iconView: UIImageView): UIView =
        UIView().apply {
            addSubview(iconView)
        }

    private fun buildTitleLabel(version: Version, language: Language, isDarkMode: Boolean, applyComModStyles: Boolean): UILabel =
        UILabel().apply {
            text = version.title.localize(language)
            font = UIFont.boldSystemFontOfSize(22.0)
            textAlignment = NSTextAlignmentCenter
            if (applyComModStyles) {
                textColor = if (isDarkMode) UIColor.whiteColor else UIColor.blackColor
            }
            numberOfLines = 0
            isAccessibilityElement = true
            accessibilityTraits = accessibilityTraits or UIAccessibilityTraitHeader
        }

    private fun buildMessageLabel(version: Version, language: Language, isDarkMode: Boolean, applyComModStyles: Boolean): UILabel =
        UILabel().apply {
            text = version.message.localize(language)
            numberOfLines = 0
            font = UIFont.systemFontOfSize(16.0)
            textAlignment = NSTextAlignmentCenter
            if (applyComModStyles) {
                textColor = if (isDarkMode) UIColor.whiteColor else UIColor.blackColor
            }
            isAccessibilityElement = true
        }

    private fun buildPrimaryButton(version: Version, language: Language, isDarkMode: Boolean, applyComModStyles: Boolean): UIButton =
        UIButton().apply {
            setTitle(version.ok.localize(language), forState = UIControlStateNormal)
            if (applyComModStyles) {
                backgroundColor = if (isDarkMode) UIColor.whiteColor else VERY_DARK_GREY
                setTitleColor(if (isDarkMode) VERY_DARK_GREY else UIColor.whiteColor, forState = UIControlStateNormal)
            } else {
                 setTitleColor(UIColor.systemBlueColor, forState = UIControlStateNormal)
            }
            layer.cornerRadius = 24.0
            titleLabel?.font = UIFont.boldSystemFontOfSize(16.0)
            addTarget(target, action = sel_registerName("onPositiveClickAction"), forControlEvents = UIControlEventTouchUpInside.toULong())
            isAccessibilityElement = true
            accessibilityLabel = version.ok.localize(language)
        }

    private fun buildSecondaryButton(version: Version, language: Language, isDarkMode: Boolean, applyComModStyles: Boolean): UIButton =
        UIButton().apply {
            setTitle(version.cancel.localize(language), forState = UIControlStateNormal)
            if (applyComModStyles) {
                backgroundColor = UIColor.clearColor
                setTitleColor(if (isDarkMode) UIColor.whiteColor else VERY_DARK_GREY, forState = UIControlStateNormal)
                layer.borderWidth = 1.0
                layer.borderColor = if (isDarkMode) UIColor.whiteColor.CGColor else MEDIUM_LIGHT_GREY.CGColor
            } else {
                 setTitleColor(UIColor.systemBlueColor, forState = UIControlStateNormal)
            }
            layer.cornerRadius = 24.0
            
            titleLabel?.font = UIFont.boldSystemFontOfSize(16.0)
            addTarget(target, action = sel_registerName("onNegativeClickAction"), forControlEvents = UIControlEventTouchUpInside.toULong())
            isAccessibilityElement = true
            accessibilityLabel = version.cancel.localize(language)
        }

    private fun buildContentStack(
        iconContainer: UIView?,
        titleLabel: UILabel,
        messageLabel: UILabel
    ): UIStackView =
        UIStackView().apply {
            axis = UILayoutConstraintAxisVertical
            alignment = UIStackViewAlignmentFill
            spacing = 16.0
            iconContainer?.let { addArrangedSubview(it) }
            addArrangedSubview(titleLabel)
            addArrangedSubview(messageLabel)
        }

    private fun addCheckboxRow(
        stack: UIStackView,
        version: Version,
        language: Language,
        isDarkMode: Boolean,
        applyComModStyles: Boolean
    ): Pair<UISwitch, UILabel> {
        val row = UIStackView().apply {
            axis = UILayoutConstraintAxisHorizontal
            alignment = UIStackViewAlignmentCenter
            spacing = 12.0
        }

        val checkboxLabel = UILabel().apply {
            text = version.checkBoxDontShowAgain.text.localize(language)
            font = UIFont.systemFontOfSize(16.0)
            if (applyComModStyles) {
                textColor = if (isDarkMode) UIColor.whiteColor else UIColor.blackColor
            }
            numberOfLines = 0
            isAccessibilityElement = true
        }

        val switch = UISwitch()
        if (applyComModStyles && isDarkMode) {
            switch.onTintColor = UIColor.whiteColor
            switch.thumbTintColor = VERY_DARK_GREY
        }
        switch.isAccessibilityElement = true
        switch.accessibilityLabel = version.checkBoxDontShowAgain.text.localize(language)
        row.addArrangedSubview(checkboxLabel)
        row.addArrangedSubview(UIView().apply {
            setContentHuggingPriority(1.0f, UILayoutConstraintAxisHorizontal)
        })
        row.addArrangedSubview(switch)

        stack.addArrangedSubview(row)
        return switch to checkboxLabel
    }

    private fun setupContentLayout(
        containerView: UIView,
        stack: UIStackView,
        iconView: UIImageView,
        iconContainer: UIView?,
        closeButton: UIButton?
    ) {
        containerView.addSubview(stack)
        stack.translatesAutoresizingMaskIntoConstraints = false
        val constraints = mutableListOf<NSLayoutConstraint>()
        constraints.add(stack.topAnchor.constraintEqualToAnchor(containerView.topAnchor, constant = 32.0))
        constraints.add(stack.leadingAnchor.constraintEqualToAnchor(containerView.leadingAnchor, constant = 24.0))
        constraints.add(stack.trailingAnchor.constraintEqualToAnchor(containerView.trailingAnchor, constant = -24.0))
        constraints.add(stack.bottomAnchor.constraintEqualToAnchor(containerView.bottomAnchor, constant = -24.0))

        if (iconContainer != null) {
            iconView.translatesAutoresizingMaskIntoConstraints = false
            iconContainer.translatesAutoresizingMaskIntoConstraints = false
            constraints.add(iconView.widthAnchor.constraintEqualToConstant(72.0))
            constraints.add(iconView.heightAnchor.constraintEqualToConstant(72.0))
            constraints.add(iconView.centerXAnchor.constraintEqualToAnchor(iconContainer.centerXAnchor))
            constraints.add(iconView.topAnchor.constraintEqualToAnchor(iconContainer.topAnchor))
            constraints.add(iconView.bottomAnchor.constraintEqualToAnchor(iconContainer.bottomAnchor))
            constraints.add(iconContainer.heightAnchor.constraintEqualToConstant(72.0))
        }

        if (closeButton != null) {
            closeButton.translatesAutoresizingMaskIntoConstraints = false
            constraints.add(closeButton.topAnchor.constraintEqualToAnchor(containerView.topAnchor, constant = 12.0))
            constraints.add(closeButton.trailingAnchor.constraintEqualToAnchor(containerView.trailingAnchor, constant = -12.0))
            constraints.add(closeButton.widthAnchor.constraintEqualToConstant(32.0))
            constraints.add(closeButton.heightAnchor.constraintEqualToConstant(32.0))
        }
        
        // Ensure buttons have reasonable height
        stack.arrangedSubviews.filterIsInstance<UIButton>().forEach { button ->
            constraints.add(button.heightAnchor.constraintEqualToConstant(48.0))
        }

        NSLayoutConstraint.activateConstraints(constraints)
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

    private fun setupAccessibility(
        containerView: UIView,
        iconView: UIImageView,
        titleLabel: UILabel,
        messageLabel: UILabel,
        checkboxLabel: UILabel?,
        checkboxSwitch: UISwitch?,
        primaryButton: UIButton,
        secondaryButton: UIButton?,
        closeButton: UIButton?
    ) {
        containerView.shouldGroupAccessibilityChildren = true
        val elements = mutableListOf<Any>()
        closeButton?.let { elements.add(it) }
        if (iconView.image != null) {
            iconView.isAccessibilityElement = true
            iconView.accessibilityLabel = "App icon"
            elements.add(iconView)
        }
        elements.add(titleLabel)
        elements.add(messageLabel)
        checkboxLabel?.let { elements.add(it) }
        checkboxSwitch?.let { elements.add(it) }
        elements.add(primaryButton)
        secondaryButton?.let { elements.add(it) }
        containerView.accessibilityElements = elements
    }

    internal fun dismissAlert() {
        println("OSAMCommons - AlertWrapper: Dismissing alert")
        versionControlAlert?.navManager?.clearHighlight()
        versionControlAlert?.dismissViewControllerAnimated(true, completion = {
            println("OSAMCommons - AlertWrapper: Alert dismissal completion")
            versionControlAlert = null
        })
    }

    internal fun onPositiveClickAction() {
        val isChecked = checkboxSwitch?.isOn() ?: false
        onPositiveClickWithCheckbox?.invoke(isChecked)
        onPositiveClick?.invoke()
        dismissAlert()
    }

    internal fun onNegativeClickAction() {
        val isChecked = checkboxSwitch?.isOn() ?: false
        onNegativeClickWithCheckbox?.invoke(isChecked)
        dismissAlert()
    }

    internal fun onDismissAction() {
        onDismiss?.invoke()
        dismissAlert()
    }
}