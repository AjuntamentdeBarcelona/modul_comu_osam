package performance

import android.util.Log
import cat.bcn.commonmodule.performance.PerformanceMetric
import cat.bcn.commonmodule.performance.PerformanceWrapper
import com.google.firebase.perf.FirebasePerformance

class PerformanceWrapperAndroid : PerformanceWrapper {
    override fun createMetric(url: String, httpMethod: String): PerformanceMetric? {
        try{
            Log.d("PerformanceMetric", "${object {}.javaClass.enclosingMethod?.name} url: $url, httpMethod: $httpMethod")
            val metric = FirebasePerformance.getInstance().newHttpMetric(url, httpMethod)
            Log.d("PerformanceMetric", "${object {}.javaClass.enclosingMethod?.name} metric: $metric")
            val performanceMetricAndroid = PerformanceMetricAndroid(metric)
            Log.d("PerformanceMetric", "${object {}.javaClass.enclosingMethod?.name} performanceMetricAndroid: $performanceMetricAndroid")
            return performanceMetricAndroid
        } catch (t: Throwable) {
            Log.d("PerformanceMetric", "${object {}.javaClass.enclosingMethod?.name} error: $t")
        }
        return null
    }
}