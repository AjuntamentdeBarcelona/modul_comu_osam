package cat.bcn.commonmodule.ui.versioncontrol

import android.app.Activity
import android.content.Context
import cat.bcn.commonmodule.analytics.AnalyticsWrapper
import cat.bcn.commonmodule.crashlytics.CrashlyticsWrapper
import cat.bcn.commonmodule.crashlytics.InternalCrashlyticsWrapperImplementation
import cat.bcn.commonmodule.data.datasource.settings.Settings
import cat.bcn.commonmodule.model.AppInformation
import cat.bcn.commonmodule.model.DeviceInformation
import cat.bcn.commonmodule.performance.InternalPerformanceWrapperImplementation
import cat.bcn.commonmodule.performance.PerformanceWrapper
import cat.bcn.commonmodule.platform.PlatformInformation
import cat.bcn.commonmodule.ui.alert.AlertWrapper
import cat.bcn.commonmodule.ui.executor.Executor
import cat.bcn.commonmodule.platform.PlatformUtil

actual class OSAMCommons constructor(
    activity: Activity,
    context: Context,
    backendEndpoint: String,
    crashlyticsWrapper: CrashlyticsWrapper,
    performanceWrapper: PerformanceWrapper,
    analyticsWrapper: AnalyticsWrapper,
    platformUtil: PlatformUtil,
) {

    private val internal = OSAMCommonsInternal(
        backendEndpoint = backendEndpoint,
        executor = Executor(),
        settings = Settings("default", context),
        alertWrapper = AlertWrapper(activity, context),
        platformInformation = PlatformInformation(context),
        internalCrashlyticsWrapper = InternalCrashlyticsWrapperImplementation(crashlyticsWrapper),
        internalPerformanceWrapper = InternalPerformanceWrapperImplementation(performanceWrapper),
        analyticsWrapper = analyticsWrapper,
        platformUtil = platformUtil
    )

    actual fun versionControl(
        language: Language,
        f: (VersionControlResponse) -> Unit
    ) = internal.versionControl(language, f)

    actual fun rating(
        language: Language,
        f: (RatingControlResponse) -> Unit
    ) = internal.rating(language, f)

    actual fun deviceInformation(
        f: (DeviceInformationResponse, DeviceInformation?) -> Unit
    ) = internal.deviceInformation(f)

    actual fun appInformation(
        f: (AppInformationResponse, AppInformation?) -> Unit
    ) = internal.appInformation(f)
}
