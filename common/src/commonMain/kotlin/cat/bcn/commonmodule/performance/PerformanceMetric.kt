package cat.bcn.commonmodule.performance

interface PerformanceMetric {
    fun start()
    fun setRequestPayloadSize(bytes: Long)
    fun markRequestComplete()
    fun markResponseStart()
    fun setResponseContentType(contentType: String)
    fun setHttpResponseCode(responseCode: Int)
    fun setResponsePayloadSize(bytes: Long)
    fun putAttribute(attribute: String, value: String)
    fun stop()
}