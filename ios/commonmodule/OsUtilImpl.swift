import Foundation
import OSAMCommon
import UIKit

class OsUtilImpl : OsUtil {
    func encodeUrl(url: String) -> String? {
        let urlString: String? = url.addingPercentEncoding(withAllowedCharacters: .urlFragmentAllowed)
        return urlString
    }
    
    func openUrl(url: String) -> Bool {
        if let urlObj = URL(string: url) {
            UIApplication.shared.open(urlObj)
            return true
        } else {
            return false
        }
    }
}
