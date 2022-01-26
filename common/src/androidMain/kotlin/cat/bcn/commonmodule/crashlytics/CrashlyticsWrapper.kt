package cat.bcn.commonmodule.crashlytics

/**
 * This interface can be implemented in the following way:
 * ```
 * override fun recordException(exception: Exception) {
 *     FirebaseCrashlytics.getInstance().recordException(exception)
 * }
 * ```
 */
interface CrashlyticsWrapper {
    fun recordException(exception: Exception)
}