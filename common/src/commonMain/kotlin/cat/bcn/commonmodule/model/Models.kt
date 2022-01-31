package cat.bcn.commonmodule.model

import cat.bcn.commonmodule.ui.versioncontrol.Language
import kotlinx.serialization.Serializable

enum class Platform {
    ANDROID, IOS
}

@Serializable
data class VersionResponse(val data: Version)

@Serializable
data class Version(
    val id: Int,
    val appId: Int,
    val packageName: String,
    val versionCode: Long,
    val versionName: String,
    val platform: Platform,
    val comparisonMode: ComparisonMode,
    val title: Text,
    val message: Text,
    val ok: Text,
    val cancel: Text,
    val url: String
) {
    enum class ComparisonMode {
        FORCE, LAZY, INFO, NONE
    }
}

fun Text.localize(language: Language) = when (language) {
    Language.CA -> ca
    Language.ES -> es
    Language.EN -> en
}

@Serializable
data class RatingResponse(val data: Rating)

@Serializable
data class Rating(
    val id: Int,
    val appId: Int,
    val packageName: String,
    val platform: Platform,
    val minutes: Int,
    val numAperture: Int,
    val message: Text
) {
    val title: Text = Text(es = "Valorar", en = "Rate", ca = "Valorar")
    val ok: Text = Text(es = "VALORAR AHORA", en = "RATE NOW", ca = "VALORAR ARA")
    val cancel: Text = Text(es = "NO, GRACIAS", en = "NO, THANKS", ca = "NO, GRÀCIES")
    val neutral: Text = Text(es = "MÁS TARDE", en = "LATER", ca = "MÉS TARD")
}

@Serializable
data class Text(val es: String, val en: String, val ca: String)
