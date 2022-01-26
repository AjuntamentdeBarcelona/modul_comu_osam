package cat.bcn.commonmodule.crashlytics

/**
 * This interface can be implemented in the following way:
 * ```
 * import Foundation
 * import OSAMCommon
 * import FirebaseCrashlytics
 *
 * class CrashlyticsWrapperIOS: CrashlyticsWrapper {
 *     func recordException(className: String, stackTrace: String) {
 *         let exception = ExceptionModel(name: className, reason: stackTrace)
 *         Crashlytics.crashlytics().record(exceptionModel: exception)
 *     }
 * }
 * ```
 */
interface CrashlyticsWrapper {
    fun recordException(className: String, stackTrace: String)
}