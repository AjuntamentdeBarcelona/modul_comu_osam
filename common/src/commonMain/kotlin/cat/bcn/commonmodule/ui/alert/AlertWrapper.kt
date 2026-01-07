package cat.bcn.commonmodule.ui.alert

import cat.bcn.commonmodule.model.Rating
import cat.bcn.commonmodule.model.Version
import cat.bcn.commonmodule.testing.Mockable
import cat.bcn.commonmodule.ui.versioncontrol.Language

@Mockable
internal expect class AlertWrapper {

    fun showVersionControlForce(
        version: Version,
        language: Language,
        onPositiveClick: () -> Unit,
    )

    fun showVersionControlLazy(
        version: Version,
        language: Language,
        onPositiveClick: (isCheckboxChecked: Boolean) -> Unit,
        onNegativeClick: () -> Unit,
        onDismissClick: () -> Unit,
    )

    fun showVersionControlInfo(
        version: Version,
        language: Language,
        onPositiveClick: (isCheckboxChecked: Boolean) -> Unit,
        onDismissClick: () -> Unit
    )

    fun showRating(
        rating: Rating,
        language: Language,
        onRatingPopupShown: () -> Unit,
        onRatingPopupError: () -> Unit
    )

    fun isVersionControlShowing(): Boolean

    fun isRatingShowing(): Boolean

}