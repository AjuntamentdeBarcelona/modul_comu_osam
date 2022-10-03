package cat.bcn.commonmodule.ui.versioncontrol

expect class OSAMCommons {
    fun versionControl(
        language: Language,
        f: (VersionControlResponse) -> Unit
    )

    fun rating(
        language: Language,
        f: (RatingControlResponse) -> Unit
    )
}

enum class VersionControlResponse {
    ACCEPTED, DISMISSED, CANCELLED, ERROR
}

enum class RatingControlResponse {
    ACCEPTED, DISMISSED, ERROR
}

enum class Language {
    CA, ES, EN
}
