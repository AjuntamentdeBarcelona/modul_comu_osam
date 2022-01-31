package cat.bcn.commonmodule.ui.alert

import android.app.AlertDialog
import android.content.Context
import cat.bcn.commonmodule.model.Rating
import cat.bcn.commonmodule.model.Version
import cat.bcn.commonmodule.model.localize
import cat.bcn.commonmodule.ui.versioncontrol.Language

internal actual class AlertWrapper(private val context: Context) {

    actual fun showVersionControlForce(
        version: Version,
        language: Language,
        onPositiveClick: () -> Unit
    ) {
        buildCommonVersionAlert(version, language, onPositiveClick)
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
        buildCommonVersionAlert(version, language, onPositiveClick)
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
        buildCommonVersionAlert(version, language, onPositiveClick)
            .setOnCancelListener { onDismissClick() }
            .show()
    }

    actual fun showRating(
        rating: Rating,
        language: Language,
        onPositiveClick: () -> Unit,
        onNegativeClick: () -> Unit,
        onNeutralClick: () -> Unit,
        onDismissClick: () -> Unit
    ) {
        val dialogRatingTitle = rating.title.localize(language)
        val appLabel = context.applicationInfo.loadLabel(context.packageManager)
        AlertDialog.Builder(context)
            .setTitle("$dialogRatingTitle $appLabel")
            .setMessage(rating.message.localize(language))
            .setPositiveButton(rating.ok.localize(language)) { _, _ -> onPositiveClick() }
            .setNegativeButton(rating.cancel.localize(language)) { _, _ -> onNegativeClick() }
            .setNeutralButton(rating.neutral.localize(language)) { _, _ -> onNeutralClick() }
            .setOnCancelListener { onDismissClick() }
            .show()
    }

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