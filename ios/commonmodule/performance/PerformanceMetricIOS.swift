import Foundation
import OSAMCommon
import FirebasePerformance

class PerformanceMetricIOS: PerformanceMetric {
    
    let metric: HTTPMetric
    
    init(metric: HTTPMetric) {
        self.metric = metric
    }
    
    func start() {
        print("PerformanceMetricIOS start")
        metric.start()
    }
    func setRequestPayloadSize(bytes: Int64) {
        print("PerformanceMetricIOS setRequestPayloadSize")
        metric.requestPayloadSize = Int(bytes)
    }
    func markRequestComplete() {
        print("PerformanceMetricIOS markRequestComplete")
        // not used for iOs
    }
    func markResponseStart() {
        print("PerformanceMetricIOS markResponseStart")
        // not used for iOs
    }
    func setResponseContentType(contentType: String) {
        print("PerformanceMetricIOS setResponseContentType")
        metric.responseContentType = contentType
    }
    func setHttpResponseCode(responseCode: Int32) {
        print("PerformanceMetricIOS setHttpResponseCode")
        metric.responseCode = Int(responseCode)
    }
    func setResponsePayloadSize(bytes: Int64) {
        print("PerformanceMetricIOS setResponsePayloadSize")
        metric.responsePayloadSize = Int(bytes)
    }
    func putAttribute(attribute: String, value: String) {
        print("PerformanceMetricIOS putAttribute")
        metric.setValue(value, forAttribute: attribute)
    }
    func stop() {
        print("PerformanceMetricIOS stop")
        metric.stop()
    }
}
