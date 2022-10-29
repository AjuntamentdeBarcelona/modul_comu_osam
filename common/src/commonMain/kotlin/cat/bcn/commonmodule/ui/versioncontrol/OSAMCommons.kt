package cat.bcn.commonmodule.ui.versioncontrol

import cat.bcn.commonmodule.model.AppInformation
import cat.bcn.commonmodule.model.DeviceInformation

expect class OSAMCommons {
    fun versionControl(
        language: Language,
        f: (VersionControlResponse) -> Unit
    )

    fun rating(
        language: Language,
        f: (RatingControlResponse) -> Unit
    )

    fun deviceInformation(
        f: (DeviceInformationResponse, DeviceInformation?) -> Unit
    )

    fun appInformation(
        f: (AppInformationResponse, AppInformation?) -> Unit
    )
}

enum class VersionControlResponse {
    ACCEPTED, DISMISSED, CANCELLED, ERROR
}

enum class RatingControlResponse {
    ACCEPTED, DISMISSED, ERROR
}

enum class DeviceInformationResponse {
    ACCEPTED, ERROR
}

enum class AppInformationResponse {
    ACCEPTED, DISMISSED, ERROR
}

enum class Language {
    CA, ES, EN
}
