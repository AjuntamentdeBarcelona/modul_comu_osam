package cat.bcn.commonmodule.ui.versioncontrol

import cat.bcn.commonmodule.model.App

// TODO add view controller
actual class VersionControl constructor() {
    actual fun check(
        appId: String,
        versionCode: Int,
        platform: App.Platform,
        language: Language,
        f: (VersionControlResponse) -> Unit
    ) {
    }
}