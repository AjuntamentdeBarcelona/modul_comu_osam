@file:OptIn(DelicateCoroutinesApi::class)

package cat.bcn.commonmodule.ui.versioncontrol

import cat.bcn.commonmodule.analytics.AnalyticsWrapper
import cat.bcn.commonmodule.analytics.CommonAnalytics
import cat.bcn.commonmodule.crashlytics.InternalCrashlyticsWrapper
import cat.bcn.commonmodule.data.datasource.local.CommonPreferences
import cat.bcn.commonmodule.data.datasource.local.Preferences
import cat.bcn.commonmodule.data.datasource.remote.CommonRemote
import cat.bcn.commonmodule.data.datasource.remote.Remote
import cat.bcn.commonmodule.data.datasource.settings.Settings
import cat.bcn.commonmodule.data.repository.CommonRepository
import cat.bcn.commonmodule.data.utils.CommonRepositoryUtils
import cat.bcn.commonmodule.extensions.getCurrentDate
import cat.bcn.commonmodule.model.AppInformation
import cat.bcn.commonmodule.model.DeviceInformation
import cat.bcn.commonmodule.model.LanguageInformation
import cat.bcn.commonmodule.model.Version
import cat.bcn.commonmodule.performance.InternalPerformanceWrapper
import cat.bcn.commonmodule.platform.PlatformUtil
import cat.bcn.commonmodule.platform.PlatformInformation
import cat.bcn.commonmodule.ui.alert.AlertWrapper
import cat.bcn.commonmodule.ui.executor.Executor
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

internal class OSAMCommonsInternal(
    private val backendEndpoint: String,
    private val executor: Executor,
    settings: Settings,
    private val alertWrapper: AlertWrapper,
    private val platformInformation: PlatformInformation,
    private val internalCrashlyticsWrapper: InternalCrashlyticsWrapper,
    private val internalPerformanceWrapper: InternalPerformanceWrapper,
    analyticsWrapper: AnalyticsWrapper,
    private val platformUtil: PlatformUtil
) {
    private val preferences: Preferences by lazy { CommonPreferences(settings) }
    private val analytics: CommonAnalytics by lazy { CommonAnalytics(analyticsWrapper) }
    private val remote: Remote by lazy { CommonRemote(backendEndpoint) }
    private var currentLanguage: Language = Language.DEFAULT
    private val commonRepository: CommonRepository by lazy {
        CommonRepository(
            remote,
            preferences,
            platformInformation,
            platformUtil,
            internalPerformanceWrapper
        )
    }

    @OptIn(DelicateCoroutinesApi::class)
    fun versionControl(
        language: Language,
        f: (VersionControlResponse) -> Unit
    ) {
        currentLanguage = language
        GlobalScope.launch(executor.main) {
            if (!alertWrapper.isVersionControlShowing()) {
                try {
                    withContext(executor.bg) { commonRepository.getVersion(language) }.fold(
                        error = { commonError ->
                            internalCrashlyticsWrapper.recordException(commonError.exception)
                            f(VersionControlResponse.ERROR)
                        },
                        success = { version ->

                            val checkIfDialogIsShown = CommonRepositoryUtils.isDialogDurationOver(
                                preferences.getLastTimeUserClickedOnAcceptButton(),
                                version.dialogDisplayDuration
                            )

                            if (version.isInTimeRange()) {
                                when (version.comparisonMode) {
                                    Version.ComparisonMode.FORCE -> alertWrapper.showVersionControlForce(
                                        version = version,
                                        language = language,
                                        onPositiveClick = {
                                            f(VersionControlResponse.ACCEPTED)
                                            platformUtil.openUrl(platformUtil.encodeUrl(version.url) ?: version.url)
                                            analytics.logVersionControlPopUp(CommonAnalytics.VersionControlAction.ACCEPTED)
                                        }
                                    )

                                    Version.ComparisonMode.LAZY ->
                                        if (preferences.getCheckBoxDontShowAgainActive() && checkIfDialogIsShown) {
                                            alertWrapper.showVersionControlLazy(
                                                version = version,
                                                language = language,
                                                onPositiveClick = { isCheckBoxChecked ->
                                                    println("VersionControl - CheckBox checked: $isCheckBoxChecked")
                                                    preferences.setCheckBoxDontShowAgainActive(!isCheckBoxChecked)
                                                    preferences.setLastTimeUserClickedOnAcceptButton(getCurrentDate())
                                                    f(VersionControlResponse.ACCEPTED)
                                                    platformUtil.openUrl(platformUtil.encodeUrl(version.url) ?: version.url)
                                                    analytics.logVersionControlPopUp(CommonAnalytics.VersionControlAction.ACCEPTED)
                                                },
                                                onNegativeClick = {
                                                    f(VersionControlResponse.CANCELLED)
                                                    analytics.logVersionControlPopUp(CommonAnalytics.VersionControlAction.CANCELLED)
                                                },
                                                onDismissClick = {
                                                    f(VersionControlResponse.DISMISSED)
                                                }
                                            )
                                        }
                                        else {
                                            f(VersionControlResponse.DISMISSED)
                                        }

                                    Version.ComparisonMode.INFO -> {
                                        if(preferences.getCheckBoxDontShowAgainActive() && checkIfDialogIsShown){
                                            alertWrapper.showVersionControlInfo(
                                                version = version,
                                                language = language,
                                                onPositiveClick = { isCheckBoxChecked ->
                                                    preferences.setLastTimeUserClickedOnAcceptButton(getCurrentDate())
                                                    preferences.setCheckBoxDontShowAgainActive(!isCheckBoxChecked)
                                                    f(VersionControlResponse.DISMISSED)
                                                    analytics.logVersionControlPopUp(CommonAnalytics.VersionControlAction.ACCEPTED)
                                                },
                                                onDismissClick = {
                                                    f(VersionControlResponse.DISMISSED)
                                                }
                                            )
                                        }
                                        else {
                                            f(VersionControlResponse.DISMISSED)
                                        }
                                    }

                                    Version.ComparisonMode.NONE -> f(VersionControlResponse.DISMISSED)
                                }
                                if (version.comparisonMode != Version.ComparisonMode.NONE) {
                                    analytics.logVersionControlPopUp(CommonAnalytics.VersionControlAction.SHOWN)
                                }
                            } else {
                                f(VersionControlResponse.DISMISSED)
                            }
                        }
                    )
                } catch (e: Exception) {
                    internalCrashlyticsWrapper.recordException(e)
                    f(VersionControlResponse.ERROR)
                }
            } else {
                f(VersionControlResponse.ERROR)
            }
        }
    }

    fun rating(
        language: Language,
        f: (RatingControlResponse) -> Unit
    ) {
        GlobalScope.launch(executor.main) {
            if (!alertWrapper.isRatingShowing()) {
                try {

                    //Initialize LastDateTime if needed
                    if (preferences.getLastDatetime() == 0L) {
                        preferences.setLastDatetime(getCurrentDate())
                    }
                    preferences.setNumApertures(preferences.getNumApertures() + 1)

                    withContext(executor.bg) { commonRepository.getRating() }.fold(
                        error = { commonError ->
                            internalCrashlyticsWrapper.recordException(commonError.exception)
                            f(RatingControlResponse.ERROR)
                        },
                        success = { rating ->
                            val shouldShowRatingDialog = rating.shouldShowDialog(
                                lastDatetime = preferences.getLastDatetime(),
                                numApertures = preferences.getNumApertures(),
                                doNotShowDialog = preferences.getDontShowAgain()
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
                    )
                } catch (e: Exception) {
                    internalCrashlyticsWrapper.recordException(e)
                    f(RatingControlResponse.ERROR)
                }
            } else {
                f(RatingControlResponse.ERROR)
            }
        }
    }

    fun deviceInformation(
        f: (DeviceInformationResponse, DeviceInformation?) -> Unit
    ) {
        GlobalScope.launch(executor.main) {
            try {
                withContext(executor.bg) { commonRepository.getDeviceInformation() }.fold(
                    error = { commonError ->
                        internalCrashlyticsWrapper.recordException(commonError.exception)
                        f(DeviceInformationResponse.ERROR, null)
                    },
                    success = { deviceInformation ->
                        f(DeviceInformationResponse.ACCEPTED, deviceInformation)
                    }
                )
            } catch (e: Exception) {
                internalCrashlyticsWrapper.recordException(e)
                f(DeviceInformationResponse.ERROR, null)
            }
        }
    }

    fun appInformation(
        f: (AppInformationResponse, AppInformation?) -> Unit
    ) {
        GlobalScope.launch(executor.main) {
            try {
                withContext(executor.bg) { commonRepository.getAppInformation() }.fold(
                    error = { commonError ->
                        internalCrashlyticsWrapper.recordException(commonError.exception)
                        f(AppInformationResponse.ERROR, null)
                    },
                    success = { appInformation ->
                        f(AppInformationResponse.ACCEPTED, appInformation)
                    }
                )
            } catch (e: Exception) {
                internalCrashlyticsWrapper.recordException(e)
                f(AppInformationResponse.ERROR, null)
            }
        }
    }

    fun changeLanguageEvent(
        language: Language,
        f: (AppLanguageResponse) -> Unit
    ) {
        try {
            val oldSelectedLanguage = preferences.getSelectedLanguage()

            if (oldSelectedLanguage == language.name) {
                f(AppLanguageResponse.NOT_SENT)
            } else {
                preferences.setDisplayedLanguage(platformInformation.getDeviceLanguage())
                preferences.setSelectedLanguage(language.name)
                preferences.setPreviousLanguage(oldSelectedLanguage)

                analytics.logLanguageChange(
                    previousLanguage = preferences.getPreviousLanguage(),
                    selectedLanguage = language.name,
                    languageDisplay = preferences.getDisplayedLanguage(),
                )
                f(AppLanguageResponse.SENT)
            }
        } catch (e: Exception) {
            internalCrashlyticsWrapper.recordException(e)
            f(AppLanguageResponse.ERROR)
        }
    }

}