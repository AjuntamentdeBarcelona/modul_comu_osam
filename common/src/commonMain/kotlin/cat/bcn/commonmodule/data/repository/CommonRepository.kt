package cat.bcn.commonmodule.data.repository

import cat.bcn.commonmodule.analytics.CommonAnalytics
import cat.bcn.commonmodule.data.datasource.local.Preferences
import cat.bcn.commonmodule.data.datasource.remote.Remote
import cat.bcn.commonmodule.data.utils.CommonRepositoryUtils
import cat.bcn.commonmodule.data.utils.CommonRepositoryUtils.sendNoConnectionAnalytic
import cat.bcn.commonmodule.model.AppInformation
import cat.bcn.commonmodule.model.CommonError
import cat.bcn.commonmodule.model.DeviceInformation
import cat.bcn.commonmodule.model.Either
import cat.bcn.commonmodule.model.LanguageInformation
import cat.bcn.commonmodule.model.Rating
import cat.bcn.commonmodule.model.Text
import cat.bcn.commonmodule.model.Version
import cat.bcn.commonmodule.performance.InternalPerformanceWrapper
import cat.bcn.commonmodule.platform.PlatformInformation
import cat.bcn.commonmodule.platform.PlatformUtil
import cat.bcn.commonmodule.testing.Mockable
import cat.bcn.commonmodule.ui.versioncontrol.Language

@Mockable
internal class CommonRepository(
    private val analytics: CommonAnalytics,
    private val remote: Remote,
    private val preferences: Preferences,
    private val platformInformation: PlatformInformation,
    private val platformUtil: PlatformUtil,
    private val internalPerformanceWrapper: InternalPerformanceWrapper
) {

    suspend fun getVersion(language: Language): Either<CommonError, Version> {
        if (platformInformation.isOnline()) {
            return try {
                Either.Right(
                    CommonRepositoryUtils.getRemoteVersion(
                        remote,
                        internalPerformanceWrapper,
                        platformInformation,
                        preferences,
                        language
                    )
                )
            } catch (e: Exception) {
                // The remote call failed, typically a transient connectivity
                // failure on an unstable network. Instead of surfacing a
                // recoverable error, fall back to the cached version, mirroring
                // the offline branch below so the flow degrades gracefully.
                Either.Right(getCachedVersionOrEmpty())
            }
        } else {
            sendNoConnectionAnalytic(analytics, platformInformation)
            return Either.Right(getCachedVersionOrEmpty())
        }
    }

    /**
     * Builds the version to use when the remote source is unavailable (device
     * offline or a failed request). If there is no usable cache yet, or the app
     * has been updated since the cache was stored, the comparison mode is forced
     * to NONE so no dialog is shown based on stale data.
     */
    private fun getCachedVersionOrEmpty(): Version {
        val storedVersionCode = preferences.getVersionControlVersionCode()
        val currentVersionCode = platformInformation.getVersionCode()
        val cachedVersion = CommonRepositoryUtils.getCachedVersion(platformInformation, preferences)

        return if (storedVersionCode == 0L || storedVersionCode != currentVersionCode) {
            cachedVersion.copy(comparisonMode = Version.ComparisonMode.NONE)
        } else {
            cachedVersion
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