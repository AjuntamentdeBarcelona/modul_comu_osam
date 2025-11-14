package cat.bcn.commonmodule.data.datasource.models.dto

import cat.bcn.commonmodule.model.Platform
import cat.bcn.commonmodule.model.Version
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class VersionResponseDto(@SerialName(value = "data") val data: VersionDto)

@Serializable
internal data class VersionDto(
    @SerialName(value = "id") val id: Int,
    @SerialName(value = "appId") val appId: Int,
    @SerialName(value = "packageName") val packageName: String,
    @SerialName(value = "versionCode") val versionCode: Long,
    @SerialName(value = "versionName") val versionName: String,
    @SerialName(value = "platform") val platform: Platform,
    @SerialName(value = "comparisonMode") val comparisonMode: Version.ComparisonMode,
    @SerialName(value = "startDate") val startDate: Long? = null,
    @SerialName(value = "endDate") val endDate: Long? = null,
    @SerialName(value = "serverDate") val serverDate: Long,
    @SerialName(value = "title") val title: TextDto,
    @SerialName(value = "message") val message: TextDto,
    @SerialName(value = "ok") val ok: TextDto,
    @SerialName(value = "cancel") val cancel: TextDto,
    @SerialName(value = "url") val url: String,
    @SerialName(value = "checkBoxDontShowAgain") val isCheckBoxVisible: Boolean,
    @SerialName(value = "dialogDisplayDuration") val dialogDisplayDuration: Long,
)

@Serializable
internal data class RatingResponseDto(@SerialName(value = "data") val data: RatingDto)

@Serializable
internal data class RatingDto(
    @SerialName(value = "id") val id: Int,
    @SerialName(value = "appId") val appId: Int,
    @SerialName(value = "packageName") val packageName: String,
    @SerialName(value = "platform") val platform: Platform,
    @SerialName(value = "minutes") val minutes: Int,
    @SerialName(value = "numAperture") val numAperture: Int,
    @SerialName(value = "message") val message: TextDto,
)

@Serializable
internal data class TextDto(
    @SerialName(value = "es") val es: String,
    @SerialName(value = "en") val en: String,
    @SerialName(value = "ca") val ca: String,
)
