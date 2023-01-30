package performance

import android.util.Log
import cat.bcn.commonmodule.performance.PerformanceMetric
import cat.bcn.commonmodule.performance.PerformanceWrapper
import com.google.firebase.perf.FirebasePerformance

class PerformanceWrapperAndroid : PerformanceWrapper {
    override fun createMetric(url: String, httpMethod: String): PerformanceMetric {
        Log.d("PerformanceMetric", "${object {}.javaClass.enclosingMethod?.name} url: $url, httpMethod: $httpMethod")
        return PerformanceMetricAndroid(FirebasePerformance.getInstance().newHttpMetric(url, httpMethod))
    }
}