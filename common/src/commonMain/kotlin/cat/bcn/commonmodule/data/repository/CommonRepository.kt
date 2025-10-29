package cat.bcn.commonmodule.data.repository

import cat.bcn.commonmodule.data.datasource.local.Preferences
import cat.bcn.commonmodule.data.datasource.remote.Remote
import cat.bcn.commonmodule.extensions.getCurrentDate
import cat.bcn.commonmodule.model.*
import cat.bcn.commonmodule.performance.InternalPerformanceWrapper
import cat.bcn.commonmodule.platform.PlatformInformation
import cat.bcn.commonmodule.platform.PlatformUtil
import cat.bcn.commonmodule.ui.versioncontrol.Language

internal class CommonRepository(
    private val remote: Remote,
    private val preferences: Preferences,
    private val platformInformation: PlatformInformation,
    private val platformUtil: PlatformUtil,
    private val internalPerformanceWrapper: InternalPerformanceWrapper
) {

    suspend fun getVersion(): Either<CommonError, Version> {
        if (platformInformation.isOnline()) {
            val versionResult = try {
                remote.getVersion(
                    internalPerformanceWrapper,
                    platformInformation.getPackageName(),
                    platformInformation.getPlatform(),
                    platformInformation.getVersionCode()
                )
            } catch (e: Exception) {
                return Either.Left(CommonError(e))
            }

            preferences.setVersionControlTitleEs(versionResult.title.localize(Language.ES))
            preferences.setVersionControlTitleEn(versionResult.title.localize(Language.EN))
            preferences.setVersionControlTitleCa(versionResult.title.localize(Language.CA))
            preferences.setVersionControlMessageEs(versionResult.message.localize(Language.ES))
            preferences.setVersionControlMessageEn(versionResult.message.localize(Language.EN))
            preferences.setVersionControlMessageCa(versionResult.message.localize(Language.CA))
            preferences.setVersionControlOkEs(versionResult.ok.localize(Language.ES))
            preferences.setVersionControlOkEn(versionResult.ok.localize(Language.EN))
            preferences.setVersionControlOkCa(versionResult.ok.localize(Language.CA))
            preferences.setVersionControlCancelEs(versionResult.cancel.localize(Language.ES))
            preferences.setVersionControlCancelEn(versionResult.cancel.localize(Language.EN))
            preferences.setVersionControlCancelCa(versionResult.cancel.localize(Language.CA))
            preferences.setVersionControlUrl(versionResult.url)
            preferences.setVersionControlComparisonMode(versionResult.comparisonMode)
            preferences.setVersionStartDate(versionResult.startDate)
            preferences.setVersionEndDate(versionResult.endDate)
            preferences.setVersionControlVersionCode(platformInformation.getVersionCode())

            return Either.Right(versionResult)

        } else {
            val storedVersionCode = preferences.getVersionControlVersionCode()
            val currentVersionCode = platformInformation.getVersionCode()

            val cachedVersion = Version(
                packageName = platformInformation.getPackageName(),
                versionCode = currentVersionCode,
                versionName = platformInformation.getVersionName(),
                platform = platformInformation.getPlatform(),
                comparisonMode = preferences.getVersionControlComparisonMode(),
                startDate = preferences.getVersionStartDate(),
                endDate = preferences.getVersionEndDate(),
                serverDate = getCurrentDate(),
                title = Text(
                    es = preferences.getVersionControlTitleEs(),
                    en = preferences.getVersionControlTitleEn(),
                    ca = preferences.getVersionControlTitleCa()
                ),
                message = Text(
                    es = preferences.getVersionControlMessageEs(),
                    en = preferences.getVersionControlMessageEn(),
                    ca = preferences.getVersionControlMessageCa()
                ),
                ok = Text(
                    es = preferences.getVersionControlOkEs(),
                    en = preferences.getVersionControlOkEn(),
                    ca = preferences.getVersionControlOkCa()
                ),
                cancel = Text(
                    es = preferences.getVersionControlCancelEs(),
                    en = preferences.getVersionControlCancelEn(),
                    ca = preferences.getVersionControlCancelCa(),
                ),
                url = preferences.getVersionControlUrl()
            )

            if (storedVersionCode == 0L || storedVersionCode != currentVersionCode) {
                val emptyVersion = cachedVersion.copy(comparisonMode = Version.ComparisonMode.NONE)
                return Either.Right(emptyVersion)
            }

            return Either.Right(cachedVersion)
        }
    }

    suspend fun getRating(): Either<CommonError, Rating> {
        var rating = Rating(
            packageName = platformInformation.getPackageName(),
            platform = platformInformation.getPlatform(),
            minutes = preferences.getRatingDateInterval(),
            numAperture = preferences.getRatingNumApertures(),
            message = Text(
                es = preferences.getRatingControlMessageEs(),
                en = preferences.getRatingControlMessageEn(),
                ca = preferences.getRatingControlMessageCa()
            )
        )
        if (platformInformation.isOnline()) {
            try {
                rating = remote.getRating(
                    internalPerformanceWrapper,
                    platformInformation.getPackageName(),
                    platformInformation.getPlatform()
                )
            } catch (e: Exception) {
                return Either.Left(CommonError(e))
            }
            preferences.setRatingNumApertures(rating.numAperture)
            preferences.setRatingDateInterval(rating.minutes)
            preferences.setRatingControlMessageEs(rating.message.localize(Language.ES))
            preferences.setRatingControlMessageEn(rating.message.localize(Language.EN))
            preferences.setRatingControlMessageCa(rating.message.localize(Language.CA))
        }
        return Either.Right(rating)
    }

    suspend fun getDeviceInformation(): Either<CommonError, DeviceInformation> {
        val deviceInformation = DeviceInformation(
            platformName = platformInformation.getPlatformName(),
            platformVersion = platformInformation.getPlatformVersion(),
            platformModel = platformInformation.getPlatformModel(platformUtil),
        )
        return Either.Right(deviceInformation)
    }

    suspend fun getAppInformation(): Either<CommonError, AppInformation> {
        val appInformation = AppInformation(
            appName = platformInformation.getAppName(),
            appVersionName = platformInformation.getVersionName(),
            appVersionCode = platformInformation.getVersionCode().toString()
        )
        return Either.Right(appInformation)
    }
}