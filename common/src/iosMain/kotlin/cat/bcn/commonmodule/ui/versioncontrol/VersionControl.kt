package cat.bcn.commonmodule.ui.versioncontrol

// TODO add view controller
actual class VersionControl constructor() {
    actual fun check(
        appId: String,
        versionCode: Int,
        language: Language,
        f: (VersionControlResponse) -> Unit
    ) {
    }
}
