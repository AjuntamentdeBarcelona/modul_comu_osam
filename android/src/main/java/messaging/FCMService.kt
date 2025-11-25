package messaging

import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class FCMService : FirebaseMessagingService() {

    /**
     * Called when a message is received.
     */
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        // This is the most important log. If you don't see this, the service isn't being called.
        Log.d(TAG, "From: ${remoteMessage.from}")

        // Check if message contains a notification payload.
        remoteMessage.notification?.let {
            Log.d(TAG, "Message Notification Body: ${it.body}")
        }
    }

    /**
     * Called if the FCM registration token is updated.
     */
    override fun onNewToken(token: String) {
        Log.d(TAG, "Refreshed token: $token")
        // You can send this token to your server if needed.
    }

    companion object {
        private const val TAG = "FCMService"
    }
}