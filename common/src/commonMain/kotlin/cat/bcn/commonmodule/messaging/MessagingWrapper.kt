package cat.bcn.commonmodule.messaging

/**
 * A platform-agnostic wrapper for Firebase Cloud Messaging operations.
 */
interface MessagingWrapper {
    /**
     * Subscribes the client app to a topic.
     *
     * @param topic The name of the topic to subscribe to.
     * @throws Exception if the subscription fails.
     */
    suspend fun subscribeToTopic(topic: String)

    /**
     * Unsubscribes the client app from a topic.
     *
     * @param topic The name of the topic to unsubscribe from.
     * @throws Exception if the unsubscription fails.
     */
    suspend fun unsubscribeFromTopic(topic: String)

    /**
     * Asynchronously retrieves the current Firebase Cloud Messaging (FCM) registration token.
     *
     * @return The current FCM token as a String.
     * @throws Exception if the token cannot be retrieved.
     */
    suspend fun getToken(): String
}