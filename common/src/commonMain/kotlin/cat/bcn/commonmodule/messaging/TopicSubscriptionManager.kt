package cat.bcn.commonmodule.messaging

import cat.bcn.commonmodule.model.CommonError
import cat.bcn.commonmodule.model.Either
import cat.bcn.commonmodule.model.Topic
import cat.bcn.commonmodule.ui.versioncontrol.AppLanguageResponse
import cat.bcn.commonmodule.ui.versioncontrol.Language
import cat.bcn.commonmodule.ui.versioncontrol.SubscriptionResponse
import kotlin.coroutines.cancellation.CancellationException

/**
 * Manages the business logic for subscribing and unsubscribing from Firebase Cloud
 * Messaging topics.
 *
 * This class acts as a central handler for all topic-related operations, abstracting
 * away the details of topic name construction and the underlying network calls. It
 * relies on a platform-agnostic [MessagingWrapper] to interact with the native
 * Firebase SDKs.
 *
 * @param messagingWrapper The platform-specific implementation for executing
 *                         FCM subscribe and unsubscribe commands.
 */
internal class TopicSubscriptionManager(private val messagingWrapper: MessagingWrapper) {

    companion object {
        private const val NO_LANGUAGE_CHANGED = "Topic has not changed. No action taken."
        private const val SUBSCRIBED_TO_NEW_TOPIC = "Subscribed to new topic:"
        private const val NOT_SUBSCRIBED_ANYMORE_TO_TOPIC = "Unsubscribed from previous topic:"
        private const val ADD_SUBSCRIPTION_ERROR = "Error subscribing to new topic:"
        private const val REMOVE_SUBSCRIPTION_ERROR = "Error unsubscribing from previous topic:"
        private const val SUBSCRIPTION_ERROR = "Error changing topic:"
    }

    /**
     * Updates the Firebase Messaging topic subscription by unsubscribing from an old topic
     * and subscribing to a new one.
     *
     * This function builds topic names based on the provided `Topic` data models and
     * performs the subscribe and unsubscribe operations in parallel. It is designed to
     * handle changes in app state, such as a language or version update.
     *
     * @param newTopic The [Topic] object representing the new state to subscribe to.
     * @param oldTopic The [Topic] object representing the old state to unsubscribe from.
     * @return `true` if a subscription change was attempted, or `false` if the new and
     * old topics were identical and no action was taken. Note that `true` indicates an
     * attempt was made, not necessarily that the underlying network operations succeeded.
     */
    suspend fun subscriptionToLanguageChange(
        oldTopic: Topic,
        newTopic: Topic,
    ): Either<CommonError, Boolean> {

        // Build the topic names from the data models
        val oldTopicName = "${oldTopic.appName}_${oldTopic.versionName}_${oldTopic.versionCode}_${oldTopic.languageCode}"
        val newTopicName = "${newTopic.appName}_${newTopic.versionName}_${newTopic.versionCode}_${newTopic.languageCode}"

        // If topics are the same, do nothing.
        if (newTopicName == oldTopicName) {
            println(NO_LANGUAGE_CHANGED)
            return Either.Right(false)
        }

        return try {
            subscribeToCustomTopic(newTopicName)
            subscribeToCustomTopic(newTopic.languageCode)
            if(oldTopic.languageCode.isNotEmpty()){
                unsubscribeToCustomTopic(oldTopicName)
                unsubscribeToCustomTopic(oldTopic.languageCode)
            }
            Either.Right(true)
        } catch (e: Exception) {
            println("$SUBSCRIPTION_ERROR ${e.message}")
            Either.Left(CommonError(e))
        }
    }

    /**
     * Manages the topic subscription on app initialization or after an update.
     *
     * This function compares the required topic (based on the new app state) with the
     * previously subscribed topic. If they differ, it unsubscribes from the old
     * topic and subscribes to the new one in parallel.
     *
     * @param oldTopic The [Topic] object representing the state from the previous app session.
     * @param newTopic The [Topic] object representing the current app state.
     * @return An `Either` containing:
     * - `Right(true)` if a subscription change was successfully attempted.
     * - `Right(false)` if the topic has not changed and no action was needed.
     * - `Left(CommonError)` if an error occurred during the subscribe or unsubscribe operations.
     */
    suspend fun subscriptionToAppInitializationOrUpdates(oldTopic: Topic, newTopic: Topic): Either<CommonError, Boolean> {
        // Build the topic names from the data models
        val oldTopicName = "${oldTopic.appName}_${oldTopic.versionName}_${oldTopic.versionCode}_${oldTopic.languageCode}"
        val newTopicName = "${newTopic.appName}_${newTopic.versionName}_${newTopic.versionCode}_${newTopic.languageCode}"

        // If topics are the same, do nothing.
        if (newTopicName == oldTopicName) {
            println(NO_LANGUAGE_CHANGED)
            return Either.Right(false)
        }

        try {
            subscribeToCustomTopic(newTopicName)
            subscribeToCustomTopic(newTopic.languageCode)
            if(oldTopic.versionName.isNotEmpty()){
                unsubscribeToCustomTopic(oldTopicName)
            }
            return Either.Right(true)
        } catch (e: Exception) {
            println("$SUBSCRIPTION_ERROR ${e.message}")
            return Either.Left(CommonError(e))
        }
    }

    /**
     * Subscribes the client to a custom topic.
     *
     * This function is a direct wrapper around the underlying messaging implementation
     * to subscribe to any given topic name.
     *
     * @param topic The exact name of the topic to subscribe to.
     */
    suspend fun subscribeToCustomTopic(topic: String): Either<CommonError, SubscriptionResponse> {
        return try {
            messagingWrapper.subscribeToTopic(topic)
            println("$SUBSCRIBED_TO_NEW_TOPIC $topic")
            Either.Right(SubscriptionResponse.ACCEPTED)

        } catch (e: Exception) {
            if (e is CancellationException) throw e
            println("$ADD_SUBSCRIPTION_ERROR $topic: ${e.message}")
            Either.Left(CommonError(e))
        }
    }

    /**
     * Unsubscribes the client from a custom topic.
     *
     * This function is a direct wrapper around the underlying messaging implementation
     * to unsubscribe from any given topic name.
     *
     * @param topic The exact name of the topic to unsubscribe from.
     */
    suspend fun unsubscribeToCustomTopic(topic: String): Either<CommonError, SubscriptionResponse> {
        return try {
            messagingWrapper.unsubscribeFromTopic(topic)
            println("$NOT_SUBSCRIBED_ANYMORE_TO_TOPIC $topic")
            Either.Right(SubscriptionResponse.ACCEPTED)
        } catch (e: Exception) {
            if (e is CancellationException) throw e
            println("$REMOVE_SUBSCRIPTION_ERROR $topic: ${e.message}")
            Either.Left(CommonError(e))
        }
    }

    /**
     * Retrieves the current Firebase Cloud Messaging (FCM) registration token.
     *
     * This function uses the platform-specific [MessagingWrapper] to fetch the token
     * and wraps the result in an [Either] type for consistent error handling.
     *
     * @return An `Either` containing `Right(token)` on success, or `Left(CommonError)`
     *         if the token could not be retrieved.
     */
    suspend fun getFCMToken(): Either<CommonError, String> {
        return try {
            val token = messagingWrapper.getToken()
            Either.Right(token)
        } catch (e: Exception) {
            if (e is CancellationException) throw e
            println("Error fetching FCM token: ${e.message}")
            Either.Left(CommonError(e))
        }
    }
}