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
        print("PerformanceMetricIOS setRequestPayloadSize bytes: \(bytes)")
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
        print("PerformanceMetricIOS setResponseContentType contentType: \(contentType)")
        metric.responseContentType = contentType
    }
    func setHttpResponseCode(responseCode: Int32) {
        print("PerformanceMetricIOS setHttpResponseCode responseCode: \(responseCode)")
        metric.responseCode = Int(responseCode)
    }
    func setResponsePayloadSize(bytes: Int64) {
        print("PerformanceMetricIOS setResponsePayloadSize bytes: \(bytes)")
        metric.responsePayloadSize = Int(bytes)
    }
    func putAttribute(attribute: String, value: String) {
        print("PerformanceMetricIOS putAttribute attribute: \(attribute), value: \(value)")
        metric.setValue(value, forAttribute: attribute)
    }
    func stop() {
        print("PerformanceMetricIOS stop")
        metric.stop()
    }
}
