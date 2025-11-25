package messaging

import cat.bcn.commonmodule.messaging.MessagingWrapper
import com.google.firebase.messaging.FirebaseMessaging

class MessagingWrapperAndroid : MessagingWrapper {

    private val firebaseMessaging = FirebaseMessaging.getInstance()

    override suspend fun subscribeToTopic(topic: String) {
        firebaseMessaging.subscribeToTopic(topic)
    }

    override suspend fun unsubscribeFromTopic(topic: String) {
        firebaseMessaging.unsubscribeFromTopic(topic)
    }
}