package cat.bcn.commonmodule.ui.alert

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import cat.bcn.commonmodule.model.Rating
import cat.bcn.commonmodule.model.Version
import cat.bcn.commonmodule.testing.Mockable
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
        onPositiveClick: () -> Unit,
    ) {
        val background = uiHelper.buildDialogBackground()
        val views = uiHelper.buildVersionDialogView(
            version = version,
            language = language,
            showNegative = false,
            showClose = false,
            showCheckBox = false
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
        dialog.window?.setBackgroundDrawable(background)
    }

    actual fun showVersionControlLazy(
        version: Version,
        language: Language,
        onPositiveClick: (isCheckboxChecked: Boolean) -> Unit,
        onNegativeClick: () -> Unit,
        onDismissClick: () -> Unit,
    ) {
        val background = uiHelper.buildDialogBackground()
        val views = uiHelper.buildVersionDialogView(
            version = version,
            language = language,
            showNegative = true,
            showClose = true
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
            onNegativeClick()
            dialog.dismiss()
        }

        views.closeButton?.setOnClickListener {
            onDismissClick()
            dialog.dismiss()
        }

        dialog.setOnCancelListener { onDismissClick() }
        versionControlAlert = dialog
        dialog.show()
        dialog.window?.setBackgroundDrawable(background)
    }

    actual fun showVersionControlInfo(
        version: Version,
        language: Language,
        onPositiveClick: (isCheckboxChecked: Boolean) -> Unit,
        onDismissClick: () -> Unit,
    ) {
        val background = uiHelper.buildDialogBackground()
        val views = uiHelper.buildVersionDialogView(
            version = version,
            language = language,
            showNegative = false,
            showClose = true
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
            onDismissClick()
            dialog.dismiss()
        }

        dialog.setOnCancelListener { onDismissClick() }
        versionControlAlert = dialog
        dialog.show()
        dialog.window?.setBackgroundDrawable(background)
    }

    actual fun showRating(
        rating: Rating,
        language: Language,
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
}
