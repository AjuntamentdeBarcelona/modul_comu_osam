package cat.bcn.commonmodule.performance

interface PerformanceMetric {
    fun start()
    fun setRequestPayloadSize(bytesLongInString: String)
    fun markRequestComplete()
    fun markResponseStart()
    fun setResponseContentType(contentType: String)
    fun setHttpResponseCode(responseCode: Int)
    fun setResponsePayloadSize(bytesLongInString: String)
    fun putAttribute(attribute: String, value: String)
    fun stop()
}