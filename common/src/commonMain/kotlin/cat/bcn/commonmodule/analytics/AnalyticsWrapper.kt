package cat.bcn.commonmodule.analytics

/**
 * This interface can be implemented in Android in the following way:
 * ```
 * class AnalyticsWrapperAndroid(context: Context) : AnalyticsWrapper {
 *
 *     private val analytics = FirebaseAnalytics.getInstance(context)
 *
 *     override fun logEvent(name: String, parameters: Map<String, String>) {
 *         analytics.logEvent(name, parameters.toBundle())
 *     }
 *
 *     private fun Map<String, String>.toBundle(): Bundle =
 *         Bundle().apply {
 *             this@toBundle.forEach {
 *                 putString(it.key, it.value)
 *             }
 *         }
 * }
 * ```
 * This interface can be implemented in iOS in the following way:
 * ```
 * import Foundation
 * import OSAMCommon
 * import FirebaseAnalytics
 *
 * class AnalyticsWrapperIOS: AnalyticsWrapper {
 *     func logEvent(name: String, parameters: [String : String]) {
 *         Analytics.logEvent(name, parameters: parameters)
 *     }
 * }
 * ```
 */
interface AnalyticsWrapper {
    fun logEvent(name: String, parameters: Map<String, String>)
}