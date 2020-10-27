package cat.bcn.commonmodule.model

import cat.bcn.commonmodule.ui.versioncontrol.Language
import kotlinx.serialization.Serializable

data class App(
    val id: Int,
    val packageName: String,
    val appName: String,
    val platform: Platform,
    val image: String,
    val versions: List<Version>
) {
    enum class Platform {
        ANDROID, IOS
    }
}

data class AppItem(
    val id: Int,
    val packageName: String,
    val appName: String,
    val platform: App.Platform,
    val image: String,
    val lastVersion: Version
)

@Serializable
data class VersionResponse(val data: Version)

@Serializable
data class Version(
    val id: Int,
    val appId: Int,
    val packageName: String,
    val versionCode: Long,
    val versionName: String,
    val platform: App.Platform,
    val comparisonMode: ComparisonMode,
    val title: Text,
    val message: Text,
    val ok: Text,
    val cancel: Text,
    val url: String
) {
    enum class ComparisonMode {
        FORCE, LAZY, INFO
    }

    fun title(language: Language) = when (language) {
        Language.CA -> title.ca
        Language.ES -> title.es
        Language.EN -> title.en
    }

    fun message(language: Language) = when (language) {
        Language.CA -> message.ca
        Language.ES -> message.es
        Language.EN -> message.en
    }

    fun ok(language: Language) = when (language) {
        Language.CA -> ok.ca
        Language.ES -> ok.es
        Language.EN -> ok.en
    }

    fun cancel(language: Language) = when (language) {
        Language.CA -> cancel.ca
        Language.ES -> cancel.es
        Language.EN -> cancel.en
    }
}

data class Rating(
    val id: Int,
    val appId: Int,
    val packageName: String,
    val platform: App.Platform,
    val minutes: Int,
    val numAperture: Int,
    val message: Text,
    val url: String
)

@Serializable
data class Text(val es: String, val en: String, val ca: String)
