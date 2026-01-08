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
import cat.bcn.commonmodule.messaging.MessagingWrapper
import cat.bcn.commonmodule.messaging.TopicSubscriptionManager
import cat.bcn.commonmodule.model.AppInformation
import cat.bcn.commonmodule.model.DeviceInformation
import cat.bcn.commonmodule.model.Version
import cat.bcn.commonmodule.performance.InternalPerformanceWrapper
import cat.bcn.commonmodule.platform.PlatformUtil
import cat.bcn.commonmodule.platform.PlatformInformation
import cat.bcn.commonmodule.ui.alert.AlertWrapper
import cat.bcn.commonmodule.ui.executor.Executor
import cat.bcn.commonmodule.ui.logic.event.Event
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
    private val platformUtil: PlatformUtil,
    private val messagingWrapper: MessagingWrapper,
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
    private val topicSubscriptionManager: TopicSubscriptionManager by lazy { TopicSubscriptionManager(messagingWrapper) }
    private val event by lazy { Event(topicSubscriptionManager, preferences, platformInformation, analytics, internalCrashlyticsWrapper, executor) }


    @OptIn(DelicateCoroutinesApi::class)
    fun versionControl(
        language: Language,
        f: (VersionControlResponse) -> Unit,
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
        f: (DeviceInformationResponse, DeviceInformation?) -> Unit,
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

    /**
     * Handles all logic associated with a user changing the application language.
     *
     * This function orchestrates several actions:
     * 1. Updates local preferences to reflect the new language selection.
     * 2. Sends an analytics event to track the language change.
     * 3. Asynchronously updates the Firebase Messaging topic subscription to ensure
     *    the device receives notifications for the newly selected language.
     *
     * @param language The new [Language] selected by the user.
     * @param f A callback that returns the result of the operation, indicating
     *          whether the event was processed.
     */
    fun changeLanguageEvent(language: Language, f: (AppLanguageResponse) -> Unit, ) = event.changeLanguageEvent(language, f)

    /**
     * Manages the Firebase topic subscription when the app starts or is updated.
     *
     * This function should be called once on application startup. It determines the
     * correct Firebase Messaging topic based on the current app version and language,
     * compares it to the previously subscribed topic, and performs an update if
     * necessary. This ensures the device is always subscribed to the correct topic
     * for receiving version- and language-specific notifications.
     *
     * @param language The current language of the application, used to construct the topic name.
     * @param f A callback that returns the result of the subscription operation.
     */
    fun firstTimeOrUpdateEvent(language: Language, f: (AppLanguageResponse) -> Unit, ) = event.firstTimeOrUpdateAppEvent(language, f)

    /**
     * Subscribes the client to a custom-named Firebase Messaging topic.
     *
     * This is useful for targeted campaigns or segmenting users into groups that are
     * independent of app version or language. The operation is performed asynchronously.
     *
     * @param topic The exact name of the topic to subscribe to.
     * @param f A callback that returns the result of the subscription attempt.
     */
    fun subscribeToCustomTopic(topic: String, f: (SubscriptionResponse) -> Unit) = event.subscribeToCustomTopic(topic, f)

    /**
     * Unsubscribes the client from a custom-named Firebase Messaging topic.
     *
     * This function is the inverse of `subscribeToCustomTopic` and is used to stop
     * receiving notifications for a specific campaign or to clean up subscriptions.
     * The operation is performed asynchronously.
     *
     * @param topic The exact name of the topic to unsubscribe from.
     * @param f A callback that returns the result of the unsubscription attempt.
     */
    fun unsubscribeToCustomTopic(topic: String, f: (SubscriptionResponse) -> Unit) = event.unsubscribeToCustomTopic(topic, f)

    /**
     * Asynchronously retrieves the current Firebase Cloud Messaging (FCM) registration token.
     *
     * This function performs the token retrieval on a background thread and delivers
     * the result via a callback on the main thread, making it safe to use for UI updates.
     * The token can be used to send notifications to this specific device instance.
     *
     * @param f A callback that receives a [TokenResponse] object, which will either
     *          be [TokenResponse.Success] containing the token or [TokenResponse.Error]
     *          containing the error details.
     */
    fun getFCMToken(f: (TokenResponse) -> Unit) = event.getFCMToken(f)
}