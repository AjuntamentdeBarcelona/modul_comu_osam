package cat.bcn.commonmodule.data.datasource.settings

import platform.Foundation.NSBundle
import platform.Foundation.NSUserDefaults

actual class Settings constructor(name: String = "default") {

    private val delegate = NSUserDefaults(name)

    actual fun getLong(key: String, defValue: Long): Long = if (has(key)) delegate.integerForKey(key) else defValue
    actual fun setLong(key: String, value: Long) = delegate.setInteger(value, key)

    actual fun getInt(key: String, defValue: Int): Int = if (has(key)) delegate.integerForKey(key).toInt() else defValue
    actual fun setInt(key: String, value: Int) = delegate.setInteger(value.toLong(), key)

    actual fun getBoolean(key: String, defValue: Boolean): Boolean =
        if (has(key)) delegate.boolForKey(key) else defValue

    actual fun setBoolean(key: String, value: Boolean) = delegate.setBool(value, key)

    actual fun getString(key: String, defValue: String): String =
        if (has(key)) delegate.stringForKey(key) ?: defValue else defValue

    actual fun setString(key: String, value: String) = delegate.setObject(value, key)

    actual fun getFloat(key: String, defValue: Float): Float = if (has(key)) delegate.floatForKey(key) else defValue
    actual fun setFloat(key: String, value: Float) = delegate.setFloat(value, key)

    actual fun has(key: String): Boolean = delegate.objectForKey(key) != null

    actual fun clear() {
        NSUserDefaults.standardUserDefaults.removePersistentDomainForName(NSBundle.mainBundle.bundleIdentifier!!)
        NSUserDefaults.standardUserDefaults.synchronize()
    }

}
