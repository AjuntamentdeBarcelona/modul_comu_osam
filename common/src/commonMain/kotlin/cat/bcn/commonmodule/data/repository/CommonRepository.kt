package cat.bcn.commonmodule.data.repository

import cat.bcn.commonmodule.data.datasource.local.Preferences
import cat.bcn.commonmodule.data.datasource.remote.Remote
import cat.bcn.commonmodule.data.utils.CommonRepositoryUtils
import cat.bcn.commonmodule.model.AppInformation
import cat.bcn.commonmodule.model.CommonError
import cat.bcn.commonmodule.model.DeviceInformation
import cat.bcn.commonmodule.model.Either
import cat.bcn.commonmodule.model.Rating
import cat.bcn.commonmodule.model.Text
import cat.bcn.commonmodule.model.Version
import cat.bcn.commonmodule.performance.InternalPerformanceWrapper
import cat.bcn.commonmodule.platform.PlatformInformation
import cat.bcn.commonmodule.platform.PlatformUtil
import cat.bcn.commonmodule.ui.versioncontrol.Language

internal class CommonRepository(
    private val remote: Remote,
    private val preferences: Preferences,
    private val platformInformation: PlatformInformation,
    private val platformUtil: PlatformUtil,
    private val internalPerformanceWrapper: InternalPerformanceWrapper,
) {

    suspend fun getVersion(): Either<CommonError, Version> {
        if (platformInformation.isOnline()) {
            val versionResult = try {
                CommonRepositoryUtils.getRemoteVersion(remote, internalPerformanceWrapper, platformInformation, preferences)
            } catch (e: Exception) {
                return Either.Left(CommonError(e))
            }

            return Either.Right(versionResult)

        } else {
            val storedVersionCode = preferences.getVersionControlVersionCode()
            val currentVersionCode = platformInformation.getVersionCode()

            val cachedVersion = CommonRepositoryUtils.getCachedVersion(platformInformation, preferences)

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