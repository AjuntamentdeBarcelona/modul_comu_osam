package performance

import cat.bcn.commonmodule.performance.PerformanceMetric
import cat.bcn.commonmodule.performance.PerformanceWrapper
import com.google.firebase.perf.FirebasePerformance

class PerformanceWrapperAndroid : PerformanceWrapper {
    override fun createMetric(url: String, httpMethod: String): PerformanceMetric =
        PerformanceMetricAndroid(FirebasePerformance.getInstance().newHttpMetric(url, httpMethod))
}