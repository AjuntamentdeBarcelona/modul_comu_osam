package cat.bcn.commonmodule.ui.alert

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.os.Build
import android.view.View
import cat.bcn.commonmodule.model.Rating
import cat.bcn.commonmodule.model.Version
import cat.bcn.commonmodule.testing.Mockable
import cat.bcn.commonmodule.ui.model.VersionDialogViews
import cat.bcn.commonmodule.ui.utils.AccessibilityKeyNavigationManager
import cat.bcn.commonmodule.ui.utils.UIHelper
import cat.bcn.commonmodule.ui.versioncontrol.Language
import com.google.android.play.core.review.ReviewManagerFactory
import java.lang.ref.WeakReference

@Mockable
internal actual class AlertWrapper(activity: Activity, private val initialContext: Context) {

    private var weakRefActivity: WeakReference<Activity> = WeakReference(activity)

    // We prefer using the Activity context for dialogs to ensure correct theming
    private var contextRef: WeakReference<Context> = WeakReference(activity)

    fun updateActivity(activity: Activity) {
        weakRefActivity = WeakReference(activity)
        contextRef = WeakReference(activity)
        uiHelper = UIHelper(context)
    }

    private val context: Context
        get() = contextRef.get() ?: weakRefActivity.get() ?: initialContext

    private var versionControlAlert: AlertDialog? = null
    private var ratingAlert: AlertDialog? = null
    private var uiHelper = UIHelper(context)

    actual fun showVersionControlForce(
        version: Version,
        language: Language,
        isDarkMode: Boolean,
        applyComModStyles: Boolean,
        onPositiveClick: () -> Unit,
    ) {
        val background = if (applyComModStyles) uiHelper.buildDialogBackground(isDarkMode) else null
        val views = uiHelper.buildVersionDialogView(
            version = version,
            language = language,
            showNegative = false,
            showClose = false,
            showCheckBox = false,
            isDarkMode = isDarkMode,
            applyComModStyles = applyComModStyles
        )

        val dialog = AlertDialog.Builder(context)
            .setView(views.root)
            .setCancelable(false)
            .create()

        views.positiveButton.setOnClickListener {
            onPositiveClick()
            dialog.dismiss()
        }

        versionControlAlert = dialog
        dialog.show()
        if (background != null) {
            dialog.window?.setBackgroundDrawable(background)
        }
        configureVersionDialogAccessibility(dialog, views)
    }

    actual fun showVersionControlLazy(
        version: Version,
        language: Language,
        isDarkMode: Boolean,
        applyComModStyles: Boolean,
        onPositiveClick: (isCheckboxChecked: Boolean) -> Unit,
        onNegativeClick: (isCheckboxChecked: Boolean) -> Unit,
        onDismissClick: (isCheckboxChecked: Boolean) -> Unit,
    ) {
        val background = if (applyComModStyles) uiHelper.buildDialogBackground(isDarkMode) else null
        val views = uiHelper.buildVersionDialogView(
            version = version,
            language = language,
            showNegative = true,
            showClose = true,
            isDarkMode = isDarkMode,
            applyComModStyles = applyComModStyles
        )

        val dialog = AlertDialog.Builder(context)
            .setView(views.root)
            .setCancelable(true)
            .create()

        views.positiveButton.setOnClickListener {
            onPositiveClick(views.checkbox?.isChecked ?: false)
            dialog.dismiss()
        }

        views.negativeButton?.setOnClickListener {
            onNegativeClick(views.checkbox?.isChecked ?: false)
            dialog.dismiss()
        }

        views.closeButton?.setOnClickListener {
            onDismissClick(views.checkbox?.isChecked ?: false)
            dialog.dismiss()
        }

        dialog.setOnCancelListener { onDismissClick(views.checkbox?.isChecked ?: false) }
        versionControlAlert = dialog
        dialog.show()
        if (background != null) {
            dialog.window?.setBackgroundDrawable(background)
        }
        configureVersionDialogAccessibility(dialog, views)
    }

    actual fun showVersionControlInfo(
        version: Version,
        language: Language,
        isDarkMode: Boolean,
        applyComModStyles: Boolean,
        onPositiveClick: (isCheckboxChecked: Boolean) -> Unit,
        onDismissClick: (isCheckboxChecked: Boolean) -> Unit,
    ) {
        val background = if (applyComModStyles) uiHelper.buildDialogBackground(isDarkMode) else null
        val views = uiHelper.buildVersionDialogView(
            version = version,
            language = language,
            showNegative = false,
            showClose = true,
            isDarkMode = isDarkMode,
            applyComModStyles = applyComModStyles
        )

        val dialog = AlertDialog.Builder(context)
            .setView(views.root)
            .setCancelable(true)
            .create()

        views.positiveButton.setOnClickListener {
            onPositiveClick(views.checkbox?.isChecked ?: false)
            dialog.dismiss()
        }

        views.closeButton?.setOnClickListener {
            onDismissClick(views.checkbox?.isChecked ?: false)
            dialog.dismiss()
        }

        dialog.setOnCancelListener { onDismissClick(views.checkbox?.isChecked ?: false) }
        versionControlAlert = dialog
        dialog.show()
        if (background != null) {
            dialog.window?.setBackgroundDrawable(background)
        }
        configureVersionDialogAccessibility(dialog, views)
    }

    actual fun showRating(
        rating: Rating,
        language: Language,
        isDarkMode: Boolean,
        applyComModStyles: Boolean,
        onRatingPopupShown: () -> Unit,
        onRatingPopupError: () -> Unit,
    ) {
        val activity = weakRefActivity.get()

        if (activity != null && !activity.isFinishing && !activity.isDestroyed) {
            val manager = ReviewManagerFactory.create(context)
            val request = manager.requestReviewFlow()
            request.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val reviewInfo = task.result
                    val flow = manager.launchReviewFlow(activity, reviewInfo)
                    flow.addOnCompleteListener {
                        onRatingPopupShown()
                    }.addOnFailureListener {
                        onRatingPopupError()
                    }
                } else {
                    onRatingPopupError()
                }
            }
        } else {
            onRatingPopupError()
        }
    }

    actual fun isVersionControlShowing(): Boolean = versionControlAlert?.isShowing ?: false


    actual fun isRatingShowing(): Boolean = ratingAlert?.isShowing ?: false

    private fun configureVersionDialogAccessibility(
        dialog: AlertDialog,
        views: VersionDialogViews
    ) {
        val focusOrder = views.focusOrderViews.filter { it.visibility == View.VISIBLE }
        val keyboardOrder = views.keyboardOrderViews.filter { it.visibility == View.VISIBLE }

        ensureViewIds(focusOrder)
        ensureViewIds(keyboardOrder)
        configureKeyboardOrder(keyboardOrder)
        configureScreenReaderOrder(focusOrder)

        val navManager = AccessibilityKeyNavigationManager(keyboardOrder)
        dialog.setOnKeyListener { _, keyCode, event ->
            navManager.handleKeyEvent(event)
        }

        dialog.window?.decorView?.post {
            keyboardOrder.firstOrNull()?.requestFocus()
        }
    }

    private fun ensureViewIds(views: List<View>) {
        views.forEach {
            if (it.id == View.NO_ID) {
                it.id = View.generateViewId()
            }
        }
    }

    private fun configureKeyboardOrder(views: List<View>) {
        for (i in 0 until views.lastIndex) {
            views[i].nextFocusForwardId = views[i + 1].id
            views[i].nextFocusDownId = views[i + 1].id
        }
    }

    private fun configureScreenReaderOrder(views: List<View>) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP_MR1) return

        for (i in 1 until views.size) {
            views[i].accessibilityTraversalAfter = views[i - 1].id
        }
    }
}