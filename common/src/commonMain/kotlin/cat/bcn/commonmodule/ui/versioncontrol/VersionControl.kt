package cat.bcn.commonmodule.ui.versioncontrol

expect class VersionControl {
    fun check(
        appId: String,
        versionCode: Int,
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
