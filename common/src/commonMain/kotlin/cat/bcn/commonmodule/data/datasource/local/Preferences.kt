package cat.bcn.commonmodule.data.datasource.local


interface Preferences {
    fun clear()

    fun setNumApertures(value: Int)
    fun hasNumApertures(): Boolean
    fun getNumApertures(): Int

    fun setLastDatetime(value: Long)
    fun hasLastDatetime(): Boolean
    fun getLastDatetime(): Long

    fun setDontShowAgain(value: Boolean)
    fun hasDontShowAgain(): Boolean
    fun getDontShowAgain(): Boolean
}
