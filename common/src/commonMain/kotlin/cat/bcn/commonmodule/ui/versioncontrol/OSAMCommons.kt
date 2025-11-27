package cat.bcn.commonmodule.ui.versioncontrol

import cat.bcn.commonmodule.model.AppInformation
import cat.bcn.commonmodule.model.CommonError
import cat.bcn.commonmodule.model.DeviceInformation
import cat.bcn.commonmodule.model.LanguageInformation

expect class OSAMCommons {
    fun versionControl(
        language: Language,
        f: (VersionControlResponse) -> Unit
    )

    fun rating(
        language: Language,
        f: (RatingControlResponse) -> Unit
    )

    fun deviceInformation(
        f: (DeviceInformationResponse, DeviceInformation?) -> Unit
    )

    fun appInformation(
        f: (AppInformationResponse, AppInformation?) -> Unit
    )

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
    fun changeLanguageEvent(
        language: Language,
        f: (AppLanguageResponse) -> Unit
    )

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
    fun firstTimeOrUpdateEvent(
        language: Language,
        f: (AppLanguageResponse) -> Unit
    )

    /**
     * Subscribes the client to a custom-named Firebase Messaging topic.
     *
     * This is useful for targeted campaigns or segmenting users into groups that are
     * independent of app version or language. The operation is performed asynchronously.
     *
     * @param topic The exact name of the topic to subscribe to.
     * @param f A callback that returns the result of the subscription attempt.
     */
    fun subscribeToCustomTopic(
        topic: String,
        f: (SubscriptionResponse) -> Unit
    )

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
    fun unsubscribeToCustomTopic(
        topic: String,
        f: (SubscriptionResponse) -> Unit
    )

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
    fun getFCMToken(f: (TokenResponse) -> Unit)
}

enum class VersionControlResponse {
    ACCEPTED, DISMISSED, CANCELLED, ERROR
}

enum class RatingControlResponse {
    ACCEPTED, DISMISSED, ERROR
}

enum class DeviceInformationResponse {
    ACCEPTED, ERROR
}

enum class AppInformationResponse {
    ACCEPTED, DISMISSED, ERROR
}

enum class AppLanguageResponse {
    SUCCESS, UNCHANGED, ERROR
}

enum class SubscriptionResponse {
    ACCEPTED, ERROR
}

/**
 * Represents the result of a request to fetch the FCM token.
 *
 * This sealed class provides a type-safe way to handle both success and failure
 * cases, where the success case includes the retrieved token.
 */
sealed class TokenResponse {
    data class Success(val token: String) : TokenResponse()
    data class Error(val error: Exception) : TokenResponse()
}

enum class Language {
    CA, ES, EN;

    companion object {
        val DEFAULT = EN
        fun parse(value: String, defaultIfNotFound: Language = DEFAULT): Language {
            Language.values()
                .forEach {
                    if (it.toString().lowercase() == value.lowercase()) return it
                }
            return defaultIfNotFound
        }
    }
}