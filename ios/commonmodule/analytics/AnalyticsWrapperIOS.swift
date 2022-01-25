import Foundation
import OSAMCommon
import FirebaseAnalytics

class AnalyticsWrapperIOS: AnalyticsWrapper {
    func logEvent(name: String, parameters: [String : String]) {
        Analytics.logEvent(name, parameters: parameters)
    }
}
