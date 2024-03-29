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
        var version = Version(
            packageName = platformInformation.getPackageName(),
            versionCode = platformInformation.getVersionCode(),
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
        if (platformInformation.isOnline()) {
            try {
                version = remote.getVersion(
                    internalPerformanceWrapper,
                    platformInformation.getPackageName(),
                    platformInformation.getPlatform(),
                    platformInformation.getVersionCode()
                )
            } catch (e: Exception) {
                return Either.Left(CommonError(e))
            }
            preferences.setVersionControlTitleEs(version.title.localize(Language.ES))
            preferences.setVersionControlTitleEn(version.title.localize(Language.EN))
            preferences.setVersionControlTitleCa(version.title.localize(Language.CA))
            preferences.setVersionControlMessageEs(version.message.localize(Language.ES))
            preferences.setVersionControlMessageEn(version.message.localize(Language.EN))
            preferences.setVersionControlMessageCa(version.message.localize(Language.CA))
            preferences.setVersionControlOkEs(version.ok.localize(Language.ES))
            preferences.setVersionControlOkEn(version.ok.localize(Language.EN))
            preferences.setVersionControlOkCa(version.ok.localize(Language.CA))
            preferences.setVersionControlCancelEs(version.cancel.localize(Language.ES))
            preferences.setVersionControlCancelEn(version.cancel.localize(Language.EN))
            preferences.setVersionControlCancelCa(version.cancel.localize(Language.CA))
            preferences.setVersionControlUrl(version.url)
            preferences.setVersionControlComparisonMode(version.comparisonMode)
            preferences.setVersionStartDate(version.startDate)
            preferences.setVersionEndDate(version.endDate)
        }
        return Either.Right(version)
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