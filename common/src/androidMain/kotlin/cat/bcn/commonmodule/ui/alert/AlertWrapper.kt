package cat.bcn.commonmodule.ui.alert

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.widget.CheckBox
import android.widget.TextView
import cat.bcn.commonmodule.R
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
        onPositiveClick: () -> Unit,
    ) {
        versionControlAlert = AlertDialog.Builder(context)
            .setTitle(version.title.localize(language))
            .setMessage(version.message.localize(language))
            .setPositiveButton(version.ok.localize(language)) { _, _ -> onPositiveClick() }
            .setCancelable(false)
            .show()
    }

    actual fun showVersionControlLazy(
        version: Version,
        language: Language,
        onPositiveClick: (isCheckboxChecked: Boolean) -> Unit,
        onNegativeClick: () -> Unit,
        onDismissClick: () -> Unit,
    ) {
        val builder = buildCommonVersionAlert(version, language)
        val checkbox = setupDialogView(builder, version, language)

        versionControlAlert = builder
            .setPositiveButton(version.ok.localize(language)) { _, _ ->
                onPositiveClick(checkbox?.isChecked ?: false)
            }
            .setNegativeButton(version.cancel.localize(language)) { _, _ -> onNegativeClick() }
            .setOnCancelListener { onDismissClick() }
            .show()
    }

    actual fun showVersionControlInfo(
        version: Version,
        language: Language,
        onPositiveClick: (isCheckboxChecked: Boolean) -> Unit,
        onDismissClick: () -> Unit,
    ) {
        val builder = buildCommonVersionAlert(version, language)
        val checkbox = setupDialogView(builder, version, language)

        versionControlAlert = builder
            .setPositiveButton(version.ok.localize(language)) { _, _ ->
                onPositiveClick(checkbox?.isChecked ?: false)
            }
            .setOnCancelListener { onDismissClick() }
            .show()
    }

    actual fun showRating(
        rating: Rating,
        language: Language,
        onRatingPopupShown: () -> Unit,
        onRatingPopupError: () -> Unit
    ) {
        val activity = weakRefActivity.get()

        if (activity != null) {
            val manager = ReviewManagerFactory.create(context)
            val request = manager.requestReviewFlow()
            request.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val reviewInfo = task.result
                    val flow = manager.launchReviewFlow(activity, reviewInfo)
                    flow.addOnCompleteListener {
                        onRatingPopupShown()
                    }.addOnFailureListener() {
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

    private fun buildCommonVersionAlert(
        version: Version,
        language: Language,
    ): AlertDialog.Builder =
        AlertDialog.Builder(context)
            .setTitle(version.title.localize(language))

    private fun setupDialogView(
        builder: AlertDialog.Builder,
        version: Version,
        language: Language
    ): CheckBox? {
        // If the checkbox should not be visible, just set the message and return null.
        if (!version.checkBoxDontShowAgain.isCheckBoxVisible) {
            builder.setMessage(version.message.localize(language))
            return null
        }

        // Otherwise, inflate the custom view.
        val customView = LayoutInflater.from(context).inflate(R.layout.dialog_version_control_view, null)
        val checkbox = customView.findViewById<CheckBox>(R.id.dialog_checkbox)
        val messageText = customView.findViewById<TextView>(R.id.dialog_message)

        // Configure the views
        messageText.text = version.message.localize(language)
        checkbox.text = version.checkBoxDontShowAgain.text.localize(language)

        // Set the custom view on the dialog and nullify the default message view.
        builder.setView(customView).setMessage(null)

        return checkbox
    }

}