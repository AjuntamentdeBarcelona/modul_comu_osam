package cat.bcn.commonmodule.platform

import platform.Foundation.NSURL
import platform.UIKit.UIApplication

internal actual class PlatformAction {
    actual fun openUrl(url: String) {
        UIApplication.sharedApplication.openURL(NSURL(string = url))
    }
}