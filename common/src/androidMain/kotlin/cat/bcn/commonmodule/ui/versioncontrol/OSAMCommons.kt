package cat.bcn.commonmodule.ui.versioncontrol

import android.content.Context
import cat.bcn.commonmodule.analytics.AnalyticsWrapper
import cat.bcn.commonmodule.crashlytics.CrashlyticsWrapper
import cat.bcn.commonmodule.crashlytics.InternalCrashlyticsWrapperImplementation
import cat.bcn.commonmodule.data.datasource.settings.Settings
import cat.bcn.commonmodule.platform.PlatformAction
import cat.bcn.commonmodule.platform.PlatformInformation
import cat.bcn.commonmodule.ui.alert.AlertWrapper
import cat.bcn.commonmodule.ui.executor.Executor

actual class OSAMCommons constructor(
    context: Context,
    backendEndpoint: String,
    crashlyticsWrapper: CrashlyticsWrapper,
    analyticsWrapper: AnalyticsWrapper,
) {

    private val internal = OSAMCommonsInternal(
        backendEndpoint = backendEndpoint,
        executor = Executor(),
        settings = Settings("default", context),
        alertWrapper = AlertWrapper(context),
        platformAction = PlatformAction(context),
        platformInformation = PlatformInformation(context),
        internalCrashlyticsWrapper = InternalCrashlyticsWrapperImplementation(crashlyticsWrapper),
        analyticsWrapper = analyticsWrapper
    )

    actual fun versionControl(
        language: Language,
        f: (VersionControlResponse) -> Unit
    ) = internal.versionControl(language, f)

    actual fun rating(
        language: Language,
        f: (RatingControlResponse) -> Unit
    ) = internal.rating(language, f)
}