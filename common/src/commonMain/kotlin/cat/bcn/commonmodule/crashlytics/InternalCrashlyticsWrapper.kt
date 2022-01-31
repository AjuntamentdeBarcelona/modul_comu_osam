package cat.bcn.commonmodule.crashlytics

internal interface InternalCrashlyticsWrapper {
    fun recordException(exception: Exception)
}