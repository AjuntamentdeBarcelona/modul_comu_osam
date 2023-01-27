package cat.bcn.commonmodule.performance

internal class InternalPerformanceWrapperImplementation(private val performanceWrapper: PerformanceWrapper) :
    InternalPerformanceWrapper {

    override fun createMetric(url: String, httpMethod: String): PerformanceMetric {
        return performanceWrapper.createMetric(url, httpMethod)
    }

}