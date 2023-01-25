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
import cat.bcn.commonmodule.extensions.getCurrentDate
import cat.bcn.commonmodule.model.AppInformation
import cat.bcn.commonmodule.model.DeviceInformation
import cat.bcn.commonmodule.model.Version
import cat.bcn.commonmodule.performance.InternalPerformanceWrapper
import cat.bcn.commonmodule.platform.PlatformUtil
import cat.bcn.commonmodule.platform.PlatformInformation
import cat.bcn.commonmodule.ui.alert.AlertWrapper
import cat.bcn.commonmodule.ui.executor.Executor
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
    private val platformUtil: PlatformUtil,
) {
    private val preferences: Preferences by lazy { CommonPreferences(settings) }
    private val analytics: CommonAnalytics by lazy { CommonAnalytics(analyticsWrapper) }
    private val remote: Remote by lazy { CommonRemote(backendEndpoint) }
    private val commonRepository: CommonRepository by lazy {
        CommonRepository(
            remote,
            preferences,
            platformInformation,
            platformUtil,
            internalPerformanceWrapper
        )
    }

    fun versionControl(
        language: Language,
        f: (VersionControlResponse) -> Unit
    ) {
        GlobalScope.launch(executor.main) {
            if (!alertWrapper.isVersionControlShowing()) {
                try {
                    withContext(executor.bg) { commonRepository.getVersion() }.fold(
                        error = { commonError ->
                            internalCrashlyticsWrapper.recordException(commonError.exception)
                            f(VersionControlResponse.ERROR)
                        },
                        success = { version ->
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
                                    Version.ComparisonMode.LAZY -> alertWrapper.showVersionControlLazy(
                                        version = version,
                                        language = language,
                                        onPositiveClick = {
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
                                    Version.ComparisonMode.INFO -> alertWrapper.showVersionControlInfo(
                                        version = version,
                                        language = language,
                                        onPositiveClick = {
                                            f(VersionControlResponse.ACCEPTED)
                                            analytics.logVersionControlPopUp(CommonAnalytics.VersionControlAction.ACCEPTED)
                                        },
                                        onDismissClick = {
                                            f(VersionControlResponse.DISMISSED)
                                        }
                                    )
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
}