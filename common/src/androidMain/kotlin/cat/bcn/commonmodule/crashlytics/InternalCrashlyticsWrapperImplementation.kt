package cat.bcn.commonmodule.crashlytics

internal class InternalCrashlyticsWrapperImplementation(private val crashlyticsWrapper: CrashlyticsWrapper) :
    InternalCrashlyticsWrapper {

    override fun recordException(exception: Exception) {
        crashlyticsWrapper.recordException(exception)
    }

}