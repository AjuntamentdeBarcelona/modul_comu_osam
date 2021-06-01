package cat.bcn.commonmodule.data.datasource.local

import cat.bcn.commonmodule.data.datasource.settings.Settings

class CommonPreferences(private val settings: Settings) : Preferences {

    companion object {
        private const val NUM_APERTURES = "NUM_APERTURES"
        private const val LAST_DATETIME = "LAST_DATETIME"
        private const val DONT_SHOW_AGAIN = "DONT_SHOW_AGAIN"
    }

    override fun clear() {
        settings.clear()
    }

    override fun setRatingNumApertures(value: Int) = settings.setInt(NUM_APERTURES, value)
    override fun hasRatingNumApertures(): Boolean = settings.has(NUM_APERTURES)
    override fun getRatingNumApertures(): Int = settings.getInt(NUM_APERTURES, 5)

    override fun setRatingDateInterval(value: Int) = settings.setInt(LAST_DATETIME, value)
    override fun hasRatingDateInterval(): Boolean = settings.has(LAST_DATETIME)
    override fun getRatingDateInterval(): Int = settings.getInt(LAST_DATETIME, 2880) //2 dias en minutos



    override fun setNumApertures(value: Int) = settings.setInt(NUM_APERTURES, value)
    override fun hasNumApertures(): Boolean = settings.has(NUM_APERTURES)
    override fun getNumApertures(): Int = settings.getInt(NUM_APERTURES, 0)

    override fun setLastDatetime(value: Long) = settings.setLong(LAST_DATETIME, value)
    override fun hasLastDatetime(): Boolean = settings.has(LAST_DATETIME)
    override fun getLastDatetime(): Long = settings.getLong(LAST_DATETIME, 0)

    override fun setDontShowAgain(value: Boolean) = settings.setBoolean(DONT_SHOW_AGAIN, value)
    override fun hasDontShowAgain(): Boolean = settings.has(DONT_SHOW_AGAIN)
    override fun getDontShowAgain(): Boolean = settings.getBoolean(DONT_SHOW_AGAIN)
}
