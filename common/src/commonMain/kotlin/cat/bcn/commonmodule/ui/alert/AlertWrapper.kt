package cat.bcn.commonmodule.ui.alert

import cat.bcn.commonmodule.model.Rating
import cat.bcn.commonmodule.model.Version
import cat.bcn.commonmodule.ui.versioncontrol.Language

internal expect class AlertWrapper {

    fun showVersionControlForce(
        version: Version,
        language: Language,
        onPositiveClick: () -> Unit,
    )

    fun showVersionControlLazy(
        version: Version,
        language: Language,
        onPositiveClick: () -> Unit,
        onNegativeClick: () -> Unit,
        onDismissClick: () -> Unit,
    )

    fun showVersionControlInfo(
        version: Version,
        language: Language,
        onPositiveClick: () -> Unit,
        onDismissClick: () -> Unit
    )

    fun showRating(
        rating: Rating,
        language: Language,
        onPositiveClick: () -> Unit,
        onNegativeClick: () -> Unit,
        onNeutralClick: () -> Unit,
        onDismissClick: () -> Unit
    )

}