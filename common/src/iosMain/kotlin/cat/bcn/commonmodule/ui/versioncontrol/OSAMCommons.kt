package cat.bcn.commonmodule.ui.versioncontrol

import cat.bcn.commonmodule.analytics.AnalyticsWrapper
import cat.bcn.commonmodule.crashlytics.CrashlyticsWrapper
import cat.bcn.commonmodule.crashlytics.InternalCrashlyticsWrapperImplementation
import cat.bcn.commonmodule.data.datasource.settings.Settings
import cat.bcn.commonmodule.platform.PlatformInformation
import cat.bcn.commonmodule.ui.alert.AlertWrapper
import cat.bcn.commonmodule.ui.executor.Executor
import platform.UIKit.UIViewController
import cat.bcn.commonmodule.platform.PlatformUtil

actual class OSAMCommons constructor(
    vc: UIViewController,
    backendEndpoint: String,
    crashlyticsWrapper: CrashlyticsWrapper,
    analyticsWrapper: AnalyticsWrapper,
    platformUtil: PlatformUtil,
) {

    private val internal = OSAMCommonsInternal(
        backendEndpoint = backendEndpoint,
        executor = Executor(),
        settings = Settings("default"),
        alertWrapper = AlertWrapper(vc),
        platformInformation = PlatformInformation(),
        internalCrashlyticsWrapper = InternalCrashlyticsWrapperImplementation(crashlyticsWrapper),
        analyticsWrapper = analyticsWrapper,
        platformUtil = platformUtil
    )

    actual fun versionControl(
        language: Language,
        f: (VersionControlResponse) -> Unit
    ) = internal.versionControl(language, f)

    actual fun rating(language: Language, f: (RatingControlResponse) -> Unit) =
        internal.rating(language, f)

}
