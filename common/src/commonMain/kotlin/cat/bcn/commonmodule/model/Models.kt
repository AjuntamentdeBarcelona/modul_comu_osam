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
    val checkBoxDontShowAgain: CheckBoxDontShowAgain = CheckBoxDontShowAgain()
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
}

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

