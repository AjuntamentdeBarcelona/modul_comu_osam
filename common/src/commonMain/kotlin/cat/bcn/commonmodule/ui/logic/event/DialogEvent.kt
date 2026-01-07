package cat.bcn.commonmodule.ui.logic.event

import cat.bcn.commonmodule.analytics.CommonAnalytics
import cat.bcn.commonmodule.crashlytics.InternalCrashlyticsWrapper
import cat.bcn.commonmodule.data.datasource.local.Preferences
import cat.bcn.commonmodule.data.repository.CommonRepository
import cat.bcn.commonmodule.data.utils.CommonRepositoryUtils
import cat.bcn.commonmodule.extensions.getCurrentDate
import cat.bcn.commonmodule.model.Rating
import cat.bcn.commonmodule.model.Version
import cat.bcn.commonmodule.platform.PlatformUtil
import cat.bcn.commonmodule.ui.alert.AlertWrapper
import cat.bcn.commonmodule.ui.executor.Executor
import cat.bcn.commonmodule.ui.versioncontrol.Language
import cat.bcn.commonmodule.ui.versioncontrol.RatingControlResponse
import cat.bcn.commonmodule.ui.versioncontrol.VersionControlResponse
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

internal class DialogEvent(
    private val scope: CoroutineScope,
    private val executor: Executor,
    private val analytics: CommonAnalytics,
    private val alertWrapper: AlertWrapper,
    private val commonRepository: CommonRepository,
    private val internalCrashlyticsWrapper: InternalCrashlyticsWrapper,
    private val preferences: Preferences,
    private val platformUtil: PlatformUtil,
    private var currentLanguage: Language,
) {

    /**
     * Initiates the version control check process.
     *
     * This function fetches the latest version information from the repository asynchronously.
     * Based on the received version data, it determines whether to show a Force, Lazy, or Info update dialog,
     * or no dialog at all. It respects time ranges, dialog display duration intervals, and
     * previous user interactions (e.g., "Don't show again").
     *
     * @param language The language in which the dialog content should be displayed.
     * @param f A callback function invoked with the result of the version control operation.
     */
    @OptIn(DelicateCoroutinesApi::class)
    fun versionControl(
        language: Language,
        f: (VersionControlResponse) -> Unit,
    ) {
        currentLanguage = language
        scope.launch(executor.main) {
            if (!alertWrapper.isVersionControlShowing()) {
                try {
                    withContext(executor.bg) { commonRepository.getVersion(language) }.fold(error = { commonError ->
                        internalCrashlyticsWrapper.recordException(commonError.exception)
                        f(VersionControlResponse.ERROR)
                    }, success = { version ->
                        val checkIfDialogIsShown = CommonRepositoryUtils.isDialogDurationOver(
                            preferences.getLastTimeUserClickedOnAcceptButton(), version.dialogDisplayDuration
                        )

                        if (version.isInTimeRange()) {
                            when (version.comparisonMode) {
                                Version.ComparisonMode.FORCE -> handleForceUpdate(version, language, f)
                                Version.ComparisonMode.LAZY -> handleLazyUpdate(version, language, checkIfDialogIsShown, f)
                                Version.ComparisonMode.INFO -> handleInfoUpdate(version, language, checkIfDialogIsShown, f)
                                Version.ComparisonMode.NONE -> f(VersionControlResponse.DISMISSED)
                            }
                            if (version.comparisonMode != Version.ComparisonMode.NONE) {
                                analytics.logVersionControlPopUp(CommonAnalytics.VersionControlAction.SHOWN)
                            }
                        } else {
                            f(VersionControlResponse.DISMISSED)
                        }
                    })
                } catch (e: Exception) {
                    internalCrashlyticsWrapper.recordException(e)
                    f(VersionControlResponse.ERROR)
                }
            } else {
                f(VersionControlResponse.ERROR)
            }
        }
    }

    /**
     * Handles the logic for a forced update.
     * This displays a blocking dialog that requires the user to update the application.
     */
    private fun handleForceUpdate(
        version: Version,
        language: Language,
        f: (VersionControlResponse) -> Unit,
    ) {
        alertWrapper.showVersionControlForce(
            version = version, language = language, onPositiveClick = {
                f(VersionControlResponse.ACCEPTED)
                platformUtil.openUrl(platformUtil.encodeUrl(version.url) ?: version.url)
                analytics.logVersionControlPopUp(CommonAnalytics.VersionControlAction.ACCEPTED)
            })
    }

    /**
     * Handles the logic for a lazy (optional) update.
     * Checks if the user has previously opted out of seeing this update via the "Don't show again" preference
     * before displaying the dialog.
     */
    private fun handleLazyUpdate(
        version: Version,
        language: Language,
        checkIfDialogIsShown: Boolean,
        f: (VersionControlResponse) -> Unit,
    ) {
        if (preferences.getCheckBoxDontShowAgainActive() && checkIfDialogIsShown) {
            alertWrapper.showVersionControlLazy(version = version, language = language, onPositiveClick = { isCheckBoxChecked ->
                println("VersionControl - CheckBox checked: $isCheckBoxChecked")
                preferences.setCheckBoxDontShowAgainActive(!isCheckBoxChecked)
                preferences.setLastTimeUserClickedOnAcceptButton(getCurrentDate())
                f(VersionControlResponse.ACCEPTED)
                platformUtil.openUrl(platformUtil.encodeUrl(version.url) ?: version.url)
                analytics.logVersionControlPopUp(CommonAnalytics.VersionControlAction.ACCEPTED)
            }, onNegativeClick = {
                f(VersionControlResponse.CANCELLED)
                analytics.logVersionControlPopUp(CommonAnalytics.VersionControlAction.CANCELLED)
            }, onDismissClick = {
                f(VersionControlResponse.DISMISSED)
            })
        } else {
            f(VersionControlResponse.DISMISSED)
        }
    }

    /**
     * Handles the logic for an informational update.
     * Similar to lazy updates, it respects the "Don't show again" preference but is intended for
     * non-critical information or minor updates.
     */
    private fun handleInfoUpdate(
        version: Version,
        language: Language,
        checkIfDialogIsShown: Boolean,
        f: (VersionControlResponse) -> Unit,
    ) {
        if (preferences.getCheckBoxDontShowAgainActive() && checkIfDialogIsShown) {
            alertWrapper.showVersionControlInfo(version = version, language = language, onPositiveClick = { isCheckBoxChecked ->
                preferences.setLastTimeUserClickedOnAcceptButton(getCurrentDate())
                preferences.setCheckBoxDontShowAgainActive(!isCheckBoxChecked)
                f(VersionControlResponse.DISMISSED)
                analytics.logVersionControlPopUp(CommonAnalytics.VersionControlAction.ACCEPTED)
            }, onDismissClick = {
                f(VersionControlResponse.DISMISSED)
            })
        } else {
            f(VersionControlResponse.DISMISSED)
        }
    }

    /**
     * Initiates the rating check process.
     *
     * This function increments the app opening counter and fetches the rating configuration
     * from the repository. It then determines if the rating dialog should be shown based on
     * the configuration (e.g., number of app opens, time elapsed) and user preferences.
     *
     * @param language The language in which the dialog content should be displayed.
     * @param f A callback function invoked with the result of the rating operation.
     */
    @OptIn(DelicateCoroutinesApi::class)
    fun rating(
        language: Language,
        f: (RatingControlResponse) -> Unit,
    ) {
        scope.launch(executor.main) {
            if (!alertWrapper.isRatingShowing()) {
                try {
                    // Initialize LastDatetime if needed
                    if (preferences.getLastDatetime() == 0L) {
                        preferences.setLastDatetime(getCurrentDate())
                    }
                    preferences.setNumApertures(preferences.getNumApertures() + 1)

                    withContext(executor.bg) { commonRepository.getRating() }.fold(error = { commonError ->
                        internalCrashlyticsWrapper.recordException(commonError.exception)
                        f(RatingControlResponse.ERROR)
                    }, success = { rating ->
                        handleRatingDisplay(rating, language, f)
                    })
                } catch (e: Exception) {
                    internalCrashlyticsWrapper.recordException(e)
                    f(RatingControlResponse.ERROR)
                }
            } else {
                f(RatingControlResponse.ERROR)
            }
        }
    }

    /**
     * Handles the logic for displaying the rating dialog.
     *
     * Checks if the specific criteria for showing the rating dialog (defined in the [rating] object
     * and user preferences) are met. If so, it triggers the alert wrapper to show the dialog.
     */
    private fun handleRatingDisplay(
        rating: Rating,
        language: Language,
        f: (RatingControlResponse) -> Unit,
    ) {
        val shouldShowRatingDialog = rating.shouldShowDialog(
            lastDatetime = preferences.getLastDatetime(), numApertures = preferences.getNumApertures(), doNotShowDialog = preferences.getDontShowAgain()
        )

        if (shouldShowRatingDialog) {
            alertWrapper.showRating(
                rating = rating,
                language = language,
                onRatingPopupShown = {
                    preferences.setLastDatetime(getCurrentDate())
                    if (preferences.getNumApertures() >= rating.numAperture) {
                        preferences.setNumApertures(0)
                    }
                    analytics.logRatingPopUp(CommonAnalytics.RatingAction.SHOWN)
                    f(RatingControlResponse.ACCEPTED)
                },
                onRatingPopupError = {
                    f(RatingControlResponse.ERROR)
                },
            )
        } else {
            f(RatingControlResponse.DISMISSED)
        }
    }
}