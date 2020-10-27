package cat.bcn.commonmodule.ui.versioncontrol

import cat.bcn.commonmodule.model.App
import cat.bcn.commonmodule.model.Version

expect class VersionControl {
    fun check(
        appId: String,
        versionCode: Int,
        platform: App.Platform,
        language: Language,
        f: (VersionControlResponse) -> Unit
    )
}

enum class VersionControlResponse {
    ACCEPTED, DISMISSED, CANCELLED, ERROR
}

enum class Language {
    CA, ES, EN
}
