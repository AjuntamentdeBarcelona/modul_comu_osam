import Foundation
import OSAMCommon
import FirebasePerformance

class PerformanceWrapperIOS: PerformanceWrapper {
    func createMetric(url: String, httpMethod: String) -> PerformanceMetric? {
        do {
            print("PerformanceWrapperIOS createMetric start")
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

            print("PerformanceWrapperIOS createMetric 1 url: \(url), httpMethod: \(httpMethod), httpMethodType: \(httpMethodType)")
            let urlObj: URL? = URL(string: url)
            if let urlObj2 = urlObj {
                print("PerformanceWrapperIOS createMetric 2 urlObj2: \(urlObj2)")
                let metric: HTTPMetric? = HTTPMetric.init(
                    url: urlObj2,
                    httpMethod:
                        httpMethodType
                )
                if let metric2 = metric {
                    print("PerformanceWrapperIOS createMetric 3 metric2: \(metric2)")
                    let performanceMetricIOS = PerformanceMetricIOS(metric: metric2)
                    print("PerformanceWrapperIOS createMetric 4 performanceMetricIOS: \(performanceMetricIOS)")
                    return performanceMetricIOS
                }
            }
        } catch {
            print("PerformanceWrapperIOS Unknown error: \(error)")
        }
        
        return nil
    }
}
