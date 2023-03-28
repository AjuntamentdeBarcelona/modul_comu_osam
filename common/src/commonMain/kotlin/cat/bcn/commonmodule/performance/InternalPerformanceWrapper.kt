package cat.bcn.commonmodule.performance

internal interface InternalPerformanceWrapper {
    fun createMetric(url: String, httpMethod: String): PerformanceMetric?
}