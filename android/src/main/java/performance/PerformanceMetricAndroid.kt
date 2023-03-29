package performance

import android.util.Log
import cat.bcn.commonmodule.performance.PerformanceMetric
import com.google.firebase.perf.metrics.HttpMetric

class PerformanceMetricAndroid(val metric: HttpMetric?) : PerformanceMetric {
    override fun start() {
        Log.d("PerformanceMetric", "${object {}.javaClass.enclosingMethod?.name}")
        metric?.start()
    }

    override fun setRequestPayloadSize(bytes: Long) {
        Log.d("PerformanceMetric", "${object {}.javaClass.enclosingMethod?.name} bytes: $bytes")
        metric?.setRequestPayloadSize(bytes)
    }

    override fun markRequestComplete() {
        Log.d("PerformanceMetric", "${object {}.javaClass.enclosingMethod?.name}")
        metric?.markRequestComplete()
    }

    override fun markResponseStart() {
        Log.d("PerformanceMetric", "${object {}.javaClass.enclosingMethod?.name}")
        metric?.markResponseStart()
    }

    override fun setResponseContentType(contentType: String) {
        Log.d("PerformanceMetric", "${object {}.javaClass.enclosingMethod?.name} contentType: $contentType")
        metric?.setResponseContentType(contentType)
    }

    override fun setHttpResponseCode(responseCode: Int) {
        Log.d("PerformanceMetric", "${object {}.javaClass.enclosingMethod?.name} responseCode: $responseCode")
        metric?.setHttpResponseCode(responseCode)
    }

    override fun setResponsePayloadSize(bytes: Long) {
        Log.d("PerformanceMetric", "${object {}.javaClass.enclosingMethod?.name} bytes: $bytes")
        metric?.setResponsePayloadSize(bytes)
    }

    override fun putAttribute(attribute: String, value: String) {
        Log.d("PerformanceMetric", "${object {}.javaClass.enclosingMethod?.name} attribute: $attribute, value: $value")
        metric?.putAttribute(attribute, value)
    }

    override fun stop() {
        Log.d("PerformanceMetric", "${object {}.javaClass.enclosingMethod?.name}")
        metric?.stop()
    }
}