package analytics

import android.content.Context
import android.os.Bundle
import cat.bcn.commonmodule.analytics.AnalyticsWrapper
import com.google.firebase.analytics.FirebaseAnalytics

class AnalyticsWrapperAndroid(context: Context) : AnalyticsWrapper {

    private val analytics = FirebaseAnalytics.getInstance(context)

    override fun logEvent(name: String, parameters: Map<String, String>) {
        analytics.logEvent(name, parameters.toBundle())
    }

    private fun Map<String, String>.toBundle(): Bundle =
        Bundle().apply {
            this@toBundle.forEach {
                putString(it.key, it.value)
            }
        }
}