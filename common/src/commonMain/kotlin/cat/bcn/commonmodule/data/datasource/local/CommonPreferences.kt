package cat.bcn.commonmodule.data.datasource.local

import cat.bcn.commonmodule.data.datasource.settings.Settings

class CommonPreferences(private val settings: Settings) : Preferences {

    companion object {
        private const val NUM_APERTURES = "NUM_APERTURES"
        private const val LAST_DATETIME = "LAST_DATETIME"
    }

    override fun clear() {
        settings.clear()
    }

    override fun setNumApertures(value: Int) = settings.setInt(NUM_APERTURES, value)
    override fun hasNumApertures(): Boolean = settings.has(NUM_APERTURES)
    override fun getNumApertures(): Int = settings.getInt(NUM_APERTURES, 0)

    override fun setLastDatetime(value: Long) = settings.setLong(LAST_DATETIME, value)
    override fun hasLastDatetime(): Boolean = settings.has(LAST_DATETIME)
    override fun getLastDatetime(): Long = settings.getLong(LAST_DATETIME, 0)
}
