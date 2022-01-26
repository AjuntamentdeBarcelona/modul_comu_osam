package cat.bcn.commonmodule.data.datasource.settings

import android.content.Context

actual class Settings constructor(name: String = "default", context: Context) {

    private val preferences by lazy { context.getSharedPreferences(name, Context.MODE_PRIVATE) }

    actual fun getLong(key: String, defValue: Long): Long = preferences.getLong(key, defValue)
    actual fun setLong(key: String, value: Long) = preferences.edit().putLong(key, value).apply()

    actual fun getInt(key: String, defValue: Int): Int = preferences.getInt(key, defValue)
    actual fun setInt(key: String, value: Int) = preferences.edit().putInt(key, value).apply()

    actual fun getBoolean(key: String, defValue: Boolean): Boolean = preferences.getBoolean(key, defValue)
    actual fun setBoolean(key: String, value: Boolean) = preferences.edit().putBoolean(key, value).apply()

    actual fun getString(key: String, defValue: String): String =
        preferences.getString(key, defValue)!!    //Can't be null, because defValue is not nullable

    actual fun setString(key: String, value: String) = preferences.edit().putString(key, value).apply()

    actual fun getFloat(key: String, defValue: Float): Float = preferences.getFloat(key, defValue)
    actual fun setFloat(key: String, value: Float) = preferences.edit().putFloat(key, value).apply()

    actual fun has(key: String): Boolean = preferences.contains(key)

    actual fun clear() {
        preferences.edit().clear().apply()
    }

}
