package cat.bcn.commonmodule.performance

interface PerformanceWrapper {
    fun createMetric(url: String, httpMethod: String): PerformanceMetric
}