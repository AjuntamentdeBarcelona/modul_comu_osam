package cat.bcn.commonmodule.model

import cat.bcn.commonmodule.ui.versioncontrol.Language
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

enum class Platform {
    ANDROID, IOS
}

@Serializable
data class VersionResponse(@SerialName(value = "data") val data: Version)

@Serializable
data class Version(
    @SerialName(value = "id") val id: Int,
    @SerialName(value = "appId") val appId: Int,
    @SerialName(value = "packageName") val packageName: String,
    @SerialName(value = "versionCode") val versionCode: Long,
    @SerialName(value = "versionName") val versionName: String,
    @SerialName(value = "platform") val platform: Platform,
    @SerialName(value = "comparisonMode") val comparisonMode: ComparisonMode,
    @SerialName(value = "title") val title: Text,
    @SerialName(value = "message") val message: Text,
    @SerialName(value = "ok") val ok: Text,
    @SerialName(value = "cancel") val cancel: Text,
    @SerialName(value = "url") val url: String
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
data class RatingResponse(@SerialName(value = "data") val data: Rating)

@Serializable
data class Rating(
    @SerialName(value = "id") val id: Int,
    @SerialName(value = "appId") val appId: Int,
    @SerialName(value = "packageName") val packageName: String,
    @SerialName(value = "platform") val platform: Platform,
    @SerialName(value = "minutes") val minutes: Int,
    @SerialName(value = "numAperture") val numAperture: Int,
    @SerialName(value = "message") val message: Text
) {
    val title: Text = Text(es = "Valorar", en = "Rate", ca = "Valorar")
    val ok: Text = Text(es = "VALORAR AHORA", en = "RATE NOW", ca = "VALORAR ARA")
    val cancel: Text = Text(es = "NO, GRACIAS", en = "NO, THANKS", ca = "NO, GRÀCIES")
    val neutral: Text = Text(es = "MÁS TARDE", en = "LATER", ca = "MÉS TARD")
}

@Serializable
data class Text(
    @SerialName(value = "es") val es: String,
    @SerialName(value = "en") val en: String,
    @SerialName(value = "ca") val ca: String
)
