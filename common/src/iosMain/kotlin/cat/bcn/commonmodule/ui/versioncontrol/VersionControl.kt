package cat.bcn.commonmodule.ui.versioncontrol

import platform.UIKit.UIViewController

actual class VersionControl constructor(val vc: UIViewController) {
    actual fun check(
        appId: String,
        versionCode: Int,
        language: Language,
        f: (VersionControlResponse) -> Unit
    ) {
    }
}
