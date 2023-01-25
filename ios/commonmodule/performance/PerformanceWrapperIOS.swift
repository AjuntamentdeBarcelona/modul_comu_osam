import Foundation
import OSAMCommon

class PerformanceWrapperIOS: PerformanceWrapper {
    func createMetric() -> PerformanceMetric {
        return PerformanceMetricIOS()// TODO
    }
}
