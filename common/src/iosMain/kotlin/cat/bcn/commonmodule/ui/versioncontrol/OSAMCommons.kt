package cat.bcn.commonmodule.ui.versioncontrol

import cat.bcn.commonmodule.analytics.AnalyticsWrapper
import cat.bcn.commonmodule.crashlytics.CrashlyticsWrapper
import cat.bcn.commonmodule.crashlytics.InternalCrashlyticsWrapperImplementation
import cat.bcn.commonmodule.data.datasource.settings.Settings
import cat.bcn.commonmodule.messaging.MessagingWrapper
import cat.bcn.commonmodule.model.AppInformation
import cat.bcn.commonmodule.model.DeviceInformation
import cat.bcn.commonmodule.performance.InternalPerformanceWrapperImplementation
import cat.bcn.commonmodule.performance.PerformanceWrapper
import cat.bcn.commonmodule.platform.PlatformInformation
import cat.bcn.commonmodule.platform.PlatformUtil
import cat.bcn.commonmodule.ui.alert.AlertWrapper
import cat.bcn.commonmodule.ui.executor.Executor
import platform.UIKit.UIViewController

actual class OSAMCommons constructor(
    vc: UIViewController,
    backendEndpoint: String,
    crashlyticsWrapper: CrashlyticsWrapper,
    performanceWrapper: PerformanceWrapper,
    analyticsWrapper: AnalyticsWrapper,
    platformUtil: PlatformUtil,
    messagingWrapper: MessagingWrapper
) {

    private val internal = OSAMCommonsInternal(
        backendEndpoint = backendEndpoint,
        executor = Executor(),
        settings = Settings("default"),
        alertWrapper = AlertWrapper(vc),
        platformInformation = PlatformInformation(),
        internalCrashlyticsWrapper = InternalCrashlyticsWrapperImplementation(crashlyticsWrapper),
        internalPerformanceWrapper = InternalPerformanceWrapperImplementation(performanceWrapper),
        analyticsWrapper = analyticsWrapper,
        platformUtil = platformUtil,
        messagingWrapper = messagingWrapper
    )

    actual fun versionControl(
        language: Language,
        f: (VersionControlResponse) -> Unit
    ) = internal.versionControl(language, f)

    actual fun rating(language: Language, f: (RatingControlResponse) -> Unit) =
        internal.rating(language, f)

    actual fun deviceInformation(
        f: (DeviceInformationResponse, DeviceInformation?) -> Unit
    ) = internal.deviceInformation(f)

    actual fun appInformation(
        f: (AppInformationResponse, AppInformation?) -> Unit
    ) = internal.appInformation(f)

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
    actual fun changeLanguageEvent(
        language: Language,
        f: (AppLanguageResponse) -> Unit
    ) = internal.changeLanguageEvent(language, f)

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
    actual fun firstTimeOrUpdateEvent(
        language: Language,
        f: (AppLanguageResponse) -> Unit,
    ) = internal.firstTimeOrUpdateEvent(language, f)

    /**
     * Subscribes the client to a custom-named Firebase Messaging topic.
     *
     * This is useful for targeted campaigns or segmenting users into groups that are
     * independent of app version or language. The operation is performed asynchronously.
     *
     * @param topic The exact name of the topic to subscribe to.
     * @param f A callback that returns the result of the subscription attempt.
     */
    actual fun subscribeToCustomTopic(
        topic: String,
        f: (SubscriptionResponse) -> Unit,
    ) = internal.subscribeToCustomTopic(topic, f)

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
    actual fun unsubscribeToCustomTopic(
        topic: String,
        f: (SubscriptionResponse) -> Unit,
    ) = internal.unsubscribeToCustomTopic(topic, f)

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
    actual fun getFCMToken(f: (TokenResponse) -> Unit) = internal.getFCMToken(f)
}
