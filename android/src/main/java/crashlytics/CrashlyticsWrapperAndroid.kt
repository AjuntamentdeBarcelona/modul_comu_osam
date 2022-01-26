package crashlytics

import cat.bcn.commonmodule.crashlytics.CrashlyticsWrapper
import com.google.firebase.crashlytics.FirebaseCrashlytics

class CrashlyticsWrapperAndroid : CrashlyticsWrapper {
    override fun recordException(exception: Exception) {
        FirebaseCrashlytics.getInstance().recordException(exception)
    }
}