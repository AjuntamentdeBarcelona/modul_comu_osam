package cat.bcn.commonmodule.model

import cat.bcn.commonmodule.ui.versioncontrol.Language

enum class Platform {
    ANDROID, IOS
}

data class Version(
    val packageName: String,
    val versionCode: Long,
    val versionName: String,
    val platform: Platform,
    val comparisonMode: ComparisonMode,
    val startDate: Long,
    val endDate: Long,
    val serverDate: Long,
    val title: Text,
    val message: Text,
    val ok: Text,
    val cancel: Text,
    val url: String,
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

data class Rating(
    val packageName: String,
    val platform: Platform,
    val minutes: Int,
    val numAperture: Int,
    val message: Text,
) {
    val title: Text = Text(es = "Valorar", en = "Rate", ca = "Valorar")
    val ok: Text = Text(es = "VALORAR AHORA", en = "RATE NOW", ca = "VALORAR ARA")
    val cancel: Text = Text(es = "NO, GRACIAS", en = "NO, THANKS", ca = "NO, GRÀCIES")
    val neutral: Text = Text(es = "MÁS TARDE", en = "LATER", ca = "MÉS TARD")
}

data class Text(
    val es: String,
    val en: String,
    val ca: String,
)
