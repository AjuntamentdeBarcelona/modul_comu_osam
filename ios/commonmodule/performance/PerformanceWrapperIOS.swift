import Foundation
import OSAMCommon
import FirebasePerformance

class PerformanceWrapperIOS: PerformanceWrapper {
    func createMetric(url: String, httpMethod: String) -> PerformanceMetric {
        
        let httpMethodType: HTTPMethod
        
        switch httpMethod.lowercased() {
        case "put":
            httpMethodType = HTTPMethod.put
        case "post":
            httpMethodType = HTTPMethod.post
        case "delete":
            httpMethodType = HTTPMethod.delete
        case "head":
            httpMethodType = HTTPMethod.head
        case "patch":
            httpMethodType = HTTPMethod.patch
        case "options":
            httpMethodType = HTTPMethod.options
        case "trace":
            httpMethodType = HTTPMethod.trace
        case "connect":
            httpMethodType = HTTPMethod.connect
        default:
            httpMethodType = HTTPMethod.get
        }
        
        print("PerformanceWrapperIOS createMetric url: \(url), httpMethod: \(httpMethod)")
        return PerformanceMetricIOS(
            metric: HTTPMetric.init(
                url:
                    URL(string: url)!,
                httpMethod:
                    httpMethodType
            )!
        )
    }
}
