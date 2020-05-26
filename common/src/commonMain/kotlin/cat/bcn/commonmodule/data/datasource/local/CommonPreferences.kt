package cat.bcn.commonmodule.data.datasource.local

import cat.bcn.commonmodule.data.datasource.settings.Settings

class CommonPreferences(private val settings: Settings) : Preferences {

    override fun clear() {
        settings.clear()
    }
}
