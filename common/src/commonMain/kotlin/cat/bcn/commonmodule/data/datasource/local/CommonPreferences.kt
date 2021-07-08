package cat.bcn.commonmodule.data.datasource.local

import cat.bcn.commonmodule.data.datasource.settings.Settings
import cat.bcn.commonmodule.model.Version

class CommonPreferences(private val settings: Settings) : Preferences {

    companion object {
        private const val VERSION_TITLE_ES = "VERSION_TITLE_ES"
        private const val VERSION_TITLE_EN = "VERSION_TITLE_EN"
        private const val VERSION_TITLE_CA = "VERSION_TITLE_CA"
        private const val VERSION_MESSAGE_ES = "VERSION_MESSAGE_ES"
        private const val VERSION_MESSAGE_EN = "VERSION_MESSAGE_EN"
        private const val VERSION_MESSAGE_CA = "VERSION_MESSAGE_CA"
        private const val VERSION_OK_ES = "VERSION_OK_ES"
        private const val VERSION_OK_EN = "VERSION_OK_EN"
        private const val VERSION_OK_CA = "VERSION_OK_CA"
        private const val VERSION_CANCEL_ES = "VERSION_CANCEL_ES"
        private const val VERSION_CANCEL_EN = "VERSION_CANCEL_EN"
        private const val VERSION_CANCEL_CA = "VERSION_OK_CA"
        private const val VERSION_URL = "VERSION_URL"
        private const val VERSION_COMPARISION_MODE = "VERSION_COMPARISION_MODE"

        private const val RATING_NUM_APERTURES = "RATING_NUM_APERTURES"
        private const val RATING_DATE_INTERVAL = "RATING_DATE_INTERVAL"
        private const val NUM_APERTURES = "NUM_APERTURES"
        private const val LAST_DATETIME = "LAST_DATETIME"
        private const val DONT_SHOW_AGAIN = "DONT_SHOW_AGAIN"
    }

    override fun clear() {
        settings.clear()
    }

    override fun setVersionControlTitleEs(value: String) = settings.setString(VERSION_TITLE_ES, value)
    override fun getVersionControlTitleEs(): String = settings.getString(VERSION_TITLE_ES, "¡Actualízate!")
    override fun setVersionControlTitleEn(value: String) = settings.setString(VERSION_TITLE_EN, value)
    override fun getVersionControlTitleEn(): String = settings.getString(VERSION_TITLE_EN, "Update it!")
    override fun setVersionControlTitleCa(value: String) = settings.setString(VERSION_TITLE_CA, value)
    override fun getVersionControlTitleCa(): String = settings.getString(VERSION_TITLE_CA, "Actualitza’t!")

    override fun setVersionControlMessageEs(value: String) = settings.setString(VERSION_MESSAGE_ES, value)
    override fun getVersionControlMessageEs(): String = settings.getString(VERSION_MESSAGE_ES, "Nueva versión disponible.")
    override fun setVersionControlMessageEn(value: String) = settings.setString(VERSION_MESSAGE_EN, value)
    override fun getVersionControlMessageEn(): String = settings.getString(VERSION_MESSAGE_EN, "New version available.")
    override fun setVersionControlMessageCa(value: String) = settings.setString(VERSION_MESSAGE_CA, value)
    override fun getVersionControlMessageCa(): String = settings.getString(VERSION_MESSAGE_CA, "Nova versió disponible.")

    override fun setVersionControlOkEs(value: String) = settings.setString(VERSION_OK_ES, value)
    override fun getVersionControlOkEs(): String = settings.getString(VERSION_OK_ES, "Descargar")
    override fun setVersionControlOkEn(value: String) = settings.setString(VERSION_OK_EN, value)
    override fun getVersionControlOkEn(): String = settings.getString(VERSION_OK_EN, "Download")
    override fun setVersionControlOkCa(value: String) = settings.setString(VERSION_OK_CA, value)
    override fun getVersionControlOkCa(): String = settings.getString(VERSION_OK_CA, "Descarregar")

    override fun setVersionControlCancelEs(value: String) = settings.setString(VERSION_CANCEL_ES, value)
    override fun getVersionControlCancelEs(): String = settings.getString(VERSION_CANCEL_ES, "Cancelar")
    override fun setVersionControlCancelEn(value: String) = settings.setString(VERSION_CANCEL_EN, value)
    override fun getVersionControlCancelEn(): String = settings.getString(VERSION_CANCEL_EN, "Cancel")
    override fun setVersionControlCancelCa(value: String) = settings.setString(VERSION_CANCEL_CA, value)
    override fun getVersionControlCancelCa(): String = settings.getString(VERSION_CANCEL_CA, "Cancel·lar")

    override fun setVersionControlUrl(value: String) = settings.setString(VERSION_URL, value)
    override fun getVersionControlUrl(): String = settings.getString(VERSION_URL, "www.google.es")

    override fun setVersionControlComparisionMode(value: Version.ComparisonMode) = settings.setString(VERSION_COMPARISION_MODE, value.name)
    override fun getVersionControlComparisionMode(): Version.ComparisonMode  = Version.ComparisonMode.valueOf(settings.getString(VERSION_COMPARISION_MODE, "NONE")) //Por defecto no se muestra

    override fun setRatingNumApertures(value: Int) = settings.setInt(RATING_NUM_APERTURES, value)
    override fun hasRatingNumApertures(): Boolean = settings.has(RATING_NUM_APERTURES)
    override fun getRatingNumApertures(): Int = settings.getInt(RATING_NUM_APERTURES, 5)

    override fun setRatingDateInterval(value: Int) = settings.setInt(RATING_DATE_INTERVAL, value)
    override fun hasRatingDateInterval(): Boolean = settings.has(RATING_DATE_INTERVAL)
    override fun getRatingDateInterval(): Int = settings.getInt(RATING_DATE_INTERVAL, 2880) //2 dias en minutos

    override fun setNumApertures(value: Int) = settings.setInt(NUM_APERTURES, value)
    override fun hasNumApertures(): Boolean = settings.has(NUM_APERTURES)
    override fun getNumApertures(): Int = settings.getInt(NUM_APERTURES, 0)

    override fun setLastDatetime(value: Long) = settings.setLong(LAST_DATETIME, value)
    override fun hasLastDatetime(): Boolean = settings.has(LAST_DATETIME)
    override fun getLastDatetime(): Long = settings.getLong(LAST_DATETIME, 0)

    override fun setRatingControlMessageEs(value: String) = settings.setString(VERSION_MESSAGE_ES, value)
    override fun getRatingControlMessageEs(): String = settings.getString(VERSION_MESSAGE_ES, "Si te ha gustado esta app, valórame por favor. Gracias.")
    override fun setRatingControlMessageEn(value: String) = settings.setString(VERSION_MESSAGE_EN, value)
    override fun getRatingControlMessageEn(): String = settings.getString(VERSION_MESSAGE_EN, "If you liked this app, please rate me. Thank you.")
    override fun setRatingControlMessageCa(value: String) = settings.setString(VERSION_MESSAGE_CA, value)
    override fun getRatingControlMessageCa(): String = settings.getString(VERSION_MESSAGE_CA, "Si t'ha agradat aquesta app, valora'm si us plau. Gràcies.")

    override fun setDontShowAgain(value: Boolean) = settings.setBoolean(DONT_SHOW_AGAIN, value)
    override fun hasDontShowAgain(): Boolean = settings.has(DONT_SHOW_AGAIN)
    override fun getDontShowAgain(): Boolean = settings.getBoolean(DONT_SHOW_AGAIN)
}
