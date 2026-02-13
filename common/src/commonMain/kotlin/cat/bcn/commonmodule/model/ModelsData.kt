package cat.bcn.commonmodule.model

import cat.bcn.commonmodule.extensions.getCurrentDate
import cat.bcn.commonmodule.extensions.isDebug
import cat.bcn.commonmodule.ui.versioncontrol.Language

internal enum class Platform {
    ANDROID, IOS
}

internal data class Version(
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
    val checkBoxDontShowAgain: CheckBoxDontShowAgain = CheckBoxDontShowAgain(),
    val dialogDisplayDuration: Long = DIALOG_DISPLAY_DURATION_DEFAULT,
    val operativeSystemVersion: OperativeSystemVersion = OperativeSystemVersion(),
    val modelsData: ModelsData = ModelsData()
) {
    enum class ComparisonMode {
        FORCE, LAZY, INFO, NONE
    }

    fun isInTimeRange(): Boolean {
        if (isDebug) {
            println("Version - Start date: $startDate")
            println("Version - End date: $endDate")
            println("Version - Server date: $serverDate")
        }
        return serverDate in startDate..endDate
    }

    companion object {
        const val DIALOG_DISPLAY_DURATION_DEFAULT = 3600L
    }
}

internal data class OperativeSystemVersion(
    val osVersionComparisonMode: OperativeSystemRuleEnum = OperativeSystemRuleEnum.ALL_VERSIONS,
    val osVersion: String = ""
)

internal data class ModelsData(
    val modelComparisonMode: ModelRuleEnum = ModelRuleEnum.ALL_MODELS,
    val models: List<String> = emptyList()
)

fun Int.toOperativeSystemRuleEnum(): OperativeSystemRuleEnum = when (this) {
    -1 -> OperativeSystemRuleEnum.ALL_VERSIONS
    0 -> OperativeSystemRuleEnum.LESS_OR_EQUAL_THAN_VERSION
    1 -> OperativeSystemRuleEnum.ONLY_THIS_VERSION
    2 -> OperativeSystemRuleEnum.BIGGER_OR_EQUAL_THAN_VERSION
    else -> OperativeSystemRuleEnum.ALL_VERSIONS
}

enum class OperativeSystemRuleEnum {
    ALL_VERSIONS,
    LESS_OR_EQUAL_THAN_VERSION,
    BIGGER_OR_EQUAL_THAN_VERSION,
    ONLY_THIS_VERSION
}

fun Int.toModelRuleEnum(): ModelRuleEnum = when (this) {
    0 -> ModelRuleEnum.ALL_MODELS
    1 -> ModelRuleEnum.ONLY_THESE_MODELS
    2 -> ModelRuleEnum.NOT_THESE_MODELS
    else -> ModelRuleEnum.ALL_MODELS
}

enum class ModelRuleEnum {
    ALL_MODELS,
    ONLY_THESE_MODELS,
    NOT_THESE_MODELS
}


internal data class Topic(
    val appName: String,
    val versionName: String,
    val versionCode: Long,
    val languageCode: String
)

internal data class CheckBoxDontShowAgain(
    val isCheckBoxVisible: Boolean = false,
    val text: Text = Text(es = "", en = "", ca = "")
)

internal data class Rating(
    val packageName: String,
    val platform: Platform,
    val minutes: Int,
    val numAperture: Int,
    val message: Text,
) {
    companion object {
        private const val MILLIS_PER_MINUTE = 60 * 1000
    }

    val title: Text = Text(es = "Valorar", en = "Rate", ca = "Valorar")
    val ok: Text = Text(es = "VALORAR AHORA", en = "RATE NOW", ca = "VALORAR ARA")
    val cancel: Text = Text(es = "NO, GRACIAS", en = "NO, THANKS", ca = "NO, GRÀCIES")
    val neutral: Text = Text(es = "MÁS TARDE", en = "LATER", ca = "MÉS TARD")

    fun shouldShowDialog(
        lastDatetime: Long,
        numApertures: Int,
        doNotShowDialog: Boolean
    ): Boolean {
        val minutesBetween = (getCurrentDate() - lastDatetime).toDouble() / MILLIS_PER_MINUTE

        if (isDebug) {
            println("Rating - Minutes between: $minutesBetween")
            println("Rating - Num apertures: $numApertures")
        }

        return !doNotShowDialog && this.minutes <= minutesBetween && this.numAperture <= numApertures
    }
}

internal data class Text(
    val es: String,
    val en: String,
    val ca: String,
) {

    fun localize(language: Language) = when (language) {
        Language.CA -> ca
        Language.ES -> es
        Language.EN -> en
    }

}

data class DeviceInformation(
    val platformName: String,
    val platformVersion: String,
    val platformModel: String,
) {
    override fun toString(): String {
        return "platformName: ${platformName}, platformVersion: ${platformVersion}, platformModel: ${platformModel}"
    }
}

data class AppInformation(
    val appName: String,
    val appVersionName: String,
    val appVersionCode: String
) {
    override fun toString(): String {
        return "appName: ${appName}, appVersionName: ${appVersionName}, appVersionCode: ${appVersionCode}"
    }
}

data class LanguageInformation(
    val previousLanguage: String,
    val selectedLanguage: String,
    val displayedLanguage: String
) {
    override fun toString(): String {
        return "previousLanguage: ${previousLanguage}, selectedLanguage: ${selectedLanguage}, displayedLanguage: $displayedLanguage"
    }
}

enum class InternetNoConnection(val value: String) {
    AIRPLANE_MODE("airplane_mode"),
    NO_WIFI("no_wifi")
}
