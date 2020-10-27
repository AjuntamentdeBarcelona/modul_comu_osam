package cat.bcn.commonmodule.ui.versioncontrol

import cat.bcn.commonmodule.model.Rating
import com.soywiz.klock.DateTime
import com.soywiz.klock.DateTimeRange

expect class OSAM {
    fun versionControl(
        appId: String,
        versionCode: Int,
        language: Language,
        f: (VersionControlResponse) -> Unit
    )

    fun rating(appId: String, f: (RatingControlResponse) -> Unit)
}

enum class VersionControlResponse {
    ACCEPTED, DISMISSED, CANCELLED, ERROR
}

enum class RatingControlResponse {
    ACCEPTED, DISMISSED, CANCELLED, ERROR
}

enum class Language {
    CA, ES, EN
}

internal fun shouldShowRatingDialog(
    rating: Rating,
    lastDatetime: Long,
    numAperture: Int
): Boolean {
    val now = DateTime.now()
    val latest = DateTime.fromUnix(lastDatetime)
    val minutesBetween = DateTimeRange(from = latest, to = now).duration.minutes



    return rating.minutes <= minutesBetween || rating.numAperture <= numAperture
}
