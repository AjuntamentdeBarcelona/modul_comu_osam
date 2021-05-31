package cat.bcn.commonmodule.ui.versioncontrol

import cat.bcn.commonmodule.model.Rating
import com.soywiz.klock.DateTime
import com.soywiz.klock.DateTimeRange

expect class OSAMCommons {
    fun versionControl(
        versionCode: Int,
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
    ACCEPTED, DISMISSED, CANCELLED, LATER, ERROR
}

enum class Language {
    CA, ES, EN
}

internal fun shouldShowRatingDialog(
    rating: Rating,
    lastDatetime: Long,
    numAperture: Int,
    dontShowDialog: Boolean
): Boolean {
    val now = DateTime.now()
    val latest = DateTime.fromUnix(lastDatetime)
    val minutesBetween = DateTimeRange(from = latest, to = now).duration.minutes

    println("Minutes between: $minutesBetween")
    println("Num apertures: $numAperture")

    return !dontShowDialog && rating.minutes <= minutesBetween && rating.numAperture <= numAperture
}
