package cat.bcn.commonmodule.data.datasource.local

import cat.bcn.commonmodule.model.Version


interface Preferences {
    fun clear()

    fun setVersionControlTitleEs(value: String)
    fun getVersionControlTitleEs(): String
    fun setVersionControlTitleEn(value: String)
    fun getVersionControlTitleEn(): String
    fun setVersionControlTitleCa(value: String)
    fun getVersionControlTitleCa(): String

    fun setVersionControlMessageEs(value: String)
    fun getVersionControlMessageEs(): String
    fun setVersionControlMessageEn(value: String)
    fun getVersionControlMessageEn(): String
    fun setVersionControlMessageCa(value: String)
    fun getVersionControlMessageCa(): String

    fun setVersionControlOkEs(value: String)
    fun getVersionControlOkEs(): String
    fun setVersionControlOkEn(value: String)
    fun getVersionControlOkEn(): String
    fun setVersionControlOkCa(value: String)
    fun getVersionControlOkCa(): String

    fun setVersionControlCancelEs(value: String)
    fun getVersionControlCancelEs(): String
    fun setVersionControlCancelEn(value: String)
    fun getVersionControlCancelEn(): String
    fun setVersionControlCancelCa(value: String)
    fun getVersionControlCancelCa(): String

    fun setVersionControlUrl(value: String)
    fun getVersionControlUrl(): String

    fun setVersionControlComparisionMode(value: Version.ComparisonMode)
    fun getVersionControlComparisionMode(): Version.ComparisonMode

    fun setRatingNumApertures(value: Int)
    fun hasRatingNumApertures(): Boolean
    fun getRatingNumApertures(): Int

    fun setRatingDateInterval(value: Int)
    fun hasRatingDateInterval(): Boolean
    fun getRatingDateInterval(): Int

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
