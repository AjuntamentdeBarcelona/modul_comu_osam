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
}