package cat.bcn.commonmodule.data.datasource.settings

expect class Settings {
    fun getLong(key: String, defValue: Long = 0L): Long
    fun setLong(key: String, value: Long)

    fun getInt(key: String, defValue: Int = 0): Int
    fun setInt(key: String, value: Int)

    fun getBoolean(key: String, defValue: Boolean = false): Boolean
    fun setBoolean(key: String, value: Boolean)

    fun getString(key: String, defValue: String = ""): String
    fun setString(key: String, value: String)

    fun getFloat(key: String, defValue: Float = 0.0f): Float
    fun setFloat(key: String, value: Float)

    fun has(key: String): Boolean

    fun clear()
}
