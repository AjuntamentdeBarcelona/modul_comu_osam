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
import cat.bcn.commonmodule.messaging.MessagingWrapper
import cat.bcn.commonmodule.messaging.TopicSubscriptionManager
import cat.bcn.commonmodule.model.AppInformation
import cat.bcn.commonmodule.model.DeviceInformation
import cat.bcn.commonmodule.performance.InternalPerformanceWrapper
import cat.bcn.commonmodule.platform.PlatformUtil
import cat.bcn.commonmodule.platform.PlatformInformation
import cat.bcn.commonmodule.ui.alert.AlertWrapper
import cat.bcn.commonmodule.ui.executor.Executor
import cat.bcn.commonmodule.ui.logic.event.DialogEvent
import cat.bcn.commonmodule.ui.logic.event.SubscriptionsEvent
import cat.bcn.commonmodule.ui.logic.event.InfoEvent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.MainScope

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
    private val scope: CoroutineScope = MainScope()
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
    private val dialogEvent by lazy { DialogEvent(scope, executor, analytics, alertWrapper, commonRepository, internalCrashlyticsWrapper, preferences, platformUtil, currentLanguage) }
    private val infoEvent by lazy { InfoEvent(scope, executor, commonRepository, internalCrashlyticsWrapper) }
    private val subscriptionsEvent by lazy { SubscriptionsEvent(scope, topicSubscriptionManager, preferences, platformInformation, analytics, internalCrashlyticsWrapper, executor) }

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
    fun versionControl(language: Language, f: (VersionControlResponse) -> Unit) = dialogEvent.versionControl(language, f)

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
    fun rating(language: Language, f: (RatingControlResponse) -> Unit)  = dialogEvent.rating(language, f)

    /**
     * Retrieves device-specific information asynchronously.
     *
     * This function fetches details about the device (e.g., model, OS version) from the repository.
     * It executes the request on a background thread and delivers the result via the provided callback
     * on the main thread. Any exceptions encountered during the process are recorded via the
     * crashlytics wrapper.
     *
     * @param f A callback function invoked with the result of the operation.
     *          It receives a [DeviceInformationResponse] status (ACCEPTED or ERROR) and a nullable
     *          [DeviceInformation] object containing the data if successful.
     */
    fun deviceInformation(f: (DeviceInformationResponse, DeviceInformation?) -> Unit, ) = infoEvent.deviceInformation(f)

    /**
     * Retrieves application-specific information asynchronously.
     *
     * This function fetches details about the application (e.g., version name, version code, package name)
     * from the repository. It executes the request on a background thread and delivers the result via
     * the provided callback on the main thread. Any exceptions encountered during the process are
     * recorded via the crashlytics wrapper.
     *
     * @param f A callback function invoked with the result of the operation.
     *          It receives an [AppInformationResponse] status (ACCEPTED or ERROR) and a nullable
     *          [AppInformation] object containing the data if successful.
     */
    fun appInformation(f: (AppInformationResponse, AppInformation?) -> Unit) = infoEvent.appInformation(f)

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
    fun changeLanguageEvent(language: Language, f: (AppLanguageResponse) -> Unit, ) = subscriptionsEvent.changeLanguageEvent(language, f)

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
    fun firstTimeOrUpdateEvent(language: Language, f: (AppLanguageResponse) -> Unit, ) = subscriptionsEvent.firstTimeOrUpdateAppEvent(language, f)

    /**
     * Subscribes the client to a custom-named Firebase Messaging topic.
     *
     * This is useful for targeted campaigns or segmenting users into groups that are
     * independent of app version or language. The operation is performed asynchronously.
     *
     * @param topic The exact name of the topic to subscribe to.
     * @param f A callback that returns the result of the subscription attempt.
     */
    fun subscribeToCustomTopic(topic: String, f: (SubscriptionResponse) -> Unit) = subscriptionsEvent.subscribeToCustomTopic(topic, f)

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
    fun unsubscribeToCustomTopic(topic: String, f: (SubscriptionResponse) -> Unit) = subscriptionsEvent.unsubscribeToCustomTopic(topic, f)

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
    fun getFCMToken(f: (TokenResponse) -> Unit) = subscriptionsEvent.getFCMToken(f)
}