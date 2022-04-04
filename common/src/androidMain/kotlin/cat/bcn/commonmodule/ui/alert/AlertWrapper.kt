package cat.bcn.commonmodule.ui.alert

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import cat.bcn.commonmodule.model.Rating
import cat.bcn.commonmodule.model.Version
import cat.bcn.commonmodule.ui.versioncontrol.Language
import com.google.android.play.core.review.ReviewManagerFactory
import java.lang.ref.WeakReference

internal actual class AlertWrapper(activity: Activity, private val context: Context) {

    private val weakRefActivity: WeakReference<Activity> = WeakReference(activity)

    private var versionControlAlert: AlertDialog? = null
    private var ratingAlert: AlertDialog? = null

    actual fun showVersionControlForce(
        version: Version,
        language: Language,
        onPositiveClick: () -> Unit
    ) {
        versionControlAlert = buildCommonVersionAlert(version, language, onPositiveClick)
            .setCancelable(false)
            .show()
    }

    actual fun showVersionControlLazy(
        version: Version,
        language: Language,
        onPositiveClick: () -> Unit,
        onNegativeClick: () -> Unit,
        onDismissClick: () -> Unit
    ) {
        versionControlAlert = buildCommonVersionAlert(version, language, onPositiveClick)
            .setNegativeButton(version.cancel.localize(language)) { _, _ -> onNegativeClick() }
            .setOnCancelListener { onDismissClick() }
            .show()
    }

    actual fun showVersionControlInfo(
        version: Version,
        language: Language,
        onPositiveClick: () -> Unit,
        onDismissClick: () -> Unit
    ) {
        versionControlAlert = buildCommonVersionAlert(version, language, onPositiveClick)
            .setOnCancelListener { onDismissClick() }
            .show()
    }

    actual fun showRating(
        rating: Rating,
        language: Language,
        onRatingPopupShown: () -> Unit
    ) {
        val activity = weakRefActivity.get()

        activity?.let { currentActivity ->
            val manager = ReviewManagerFactory.create(context)
            val request = manager.requestReviewFlow()
            request.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val reviewInfo = task.result
                    val flow = manager.launchReviewFlow(currentActivity, reviewInfo)
                    flow.addOnCompleteListener {
                        onRatingPopupShown()
                    }
                }
            }
        }
    }

    actual fun isVersionControlShowing(): Boolean = versionControlAlert?.isShowing ?: false


    actual fun isRatingShowing(): Boolean = ratingAlert?.isShowing ?: false

    private fun buildCommonVersionAlert(
        version: Version,
        language: Language,
        onPositiveClick: () -> Unit
    ): AlertDialog.Builder =
        AlertDialog.Builder(context)
            .setTitle(version.title.localize(language))
            .setMessage(version.message.localize(language))
            .setPositiveButton(version.ok.localize(language)) { _, _ -> onPositiveClick() }

}