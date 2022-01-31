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
import cat.bcn.commonmodule.model.Rating
import cat.bcn.commonmodule.model.Version
import cat.bcn.commonmodule.platform.PlatformAction
import cat.bcn.commonmodule.platform.PlatformInformation
import cat.bcn.commonmodule.ui.alert.AlertWrapper
import cat.bcn.commonmodule.ui.executor.Executor
import com.soywiz.klock.DateTime
import com.soywiz.klock.DateTimeRange
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

internal class OSAMCommonsInternal(
    private val backendEndpoint: String,
    private val executor: Executor,
    settings: Settings,
    private val alertWrapper: AlertWrapper,
    private val platformAction: PlatformAction,
    private val platformInformation: PlatformInformation,
    private val internalCrashlyticsWrapper: InternalCrashlyticsWrapper,
    analyticsWrapper: AnalyticsWrapper,
) {
    private val preferences: Preferences by lazy { CommonPreferences(settings) }
    private val analytics: CommonAnalytics by lazy { CommonAnalytics(analyticsWrapper) }
    private val remote: Remote by lazy { CommonRemote(backendEndpoint) }
    private val commonRepository: CommonRepository by lazy {
        CommonRepository(
            remote,
            preferences,
            platformInformation
        )
    }

    fun versionControl(
        language: Language,
        f: (VersionControlResponse) -> Unit
    ) {
        GlobalScope.launch(executor.main) {
            try {
                withContext(executor.bg) { commonRepository.getVersion() }.fold(
                    error = { commonError ->
                        internalCrashlyticsWrapper.recordException(commonError.exception)
                    },
                    success = { version ->
                        when (version.comparisonMode) {
                            Version.ComparisonMode.FORCE -> alertWrapper.showVersionControlForce(
                                version = version,
                                language = language,
                                onPositiveClick = {
                                    f(VersionControlResponse.ACCEPTED)
                                    platformAction.openUrl(version.url)
                                    analytics.logVersionControlPopUp(CommonAnalytics.VersionControlAction.ACCEPTED)
                                }
                            )
                            Version.ComparisonMode.LAZY -> alertWrapper.showVersionControlLazy(
                                version = version,
                                language = language,
                                onPositiveClick = {
                                    f(VersionControlResponse.ACCEPTED)
                                    platformAction.openUrl(version.url)
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
                    }
                )
            } catch (e: Exception) {
                internalCrashlyticsWrapper.recordException(e)
                f(VersionControlResponse.ERROR)
            }
        }
    }

    fun rating(
        language: Language,
        f: (RatingControlResponse) -> Unit
    ) {
        GlobalScope.launch(executor.main) {
            try {

                //Initialize LastDateTime if needed
                if (preferences.getLastDatetime() == 0L) {
                    preferences.setLastDatetime(DateTime.nowUnixLong())
                }

                withContext(executor.bg) { commonRepository.getRating() }.fold(
                    error = { commonError ->
                        internalCrashlyticsWrapper.recordException(commonError.exception)
                    },
                    success = { rating ->

                        val shouldShowRatingDialog = shouldShowRatingDialog(
                            rating = rating,
                            lastDatetime = preferences.getLastDatetime(),
                            numAperture = preferences.getNumApertures(),
                            dontShowDialog = preferences.getDontShowAgain()
                        )

                        if (shouldShowRatingDialog) {
                            alertWrapper.showRating(
                                rating = rating,
                                language = language,
                                onPositiveClick = {
                                    f(RatingControlResponse.ACCEPTED)
                                    platformAction.openUrl(platformInformation.getAppsStoreUrl())
                                    analytics.logRatingPopUp(CommonAnalytics.RatingAction.ACCEPTED)
                                },
                                onNegativeClick = {
                                    f(RatingControlResponse.CANCELLED)
                                    preferences.setDontShowAgain(true)
                                    analytics.logRatingPopUp(CommonAnalytics.RatingAction.CANCELLED)
                                },
                                onNeutralClick = {
                                    f(RatingControlResponse.LATER)
                                    analytics.logRatingPopUp(CommonAnalytics.RatingAction.LATER)
                                },
                                onDismissClick = {
                                    f(RatingControlResponse.DISMISSED)
                                }
                            )
                            preferences.setLastDatetime(DateTime.nowUnixLong())
                            analytics.logRatingPopUp(CommonAnalytics.RatingAction.SHOWN)
                        } else {
                            f(RatingControlResponse.LATER)
                        }

                        if (preferences.getNumApertures() == rating.numAperture) {
                            preferences.setNumApertures(0)
                        } else {
                            preferences.setNumApertures(preferences.getNumApertures() + 1)
                        }
                    }
                )
            } catch (e: Exception) {
                internalCrashlyticsWrapper.recordException(e)
                f(RatingControlResponse.ERROR)
            }
        }
    }

    private fun shouldShowRatingDialog(
        rating: Rating,
        lastDatetime: Long,
        numAperture: Int,
        dontShowDialog: Boolean
    ): Boolean {
        val now = DateTime.now()
        val latest = DateTime.fromUnix(lastDatetime)
        val minutesBetween = DateTimeRange(from = latest, to = now).duration.minutes

        println("Minutes between: $minutesBetween")
        println("Num apertures: $numAperture")

        return !dontShowDialog && rating.minutes <= minutesBetween && rating.numAperture <= numAperture
    }
}