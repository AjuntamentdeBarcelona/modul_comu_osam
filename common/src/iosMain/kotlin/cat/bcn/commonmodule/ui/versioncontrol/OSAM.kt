package cat.bcn.commonmodule.ui.versioncontrol

import cat.bcn.commonmodule.data.datasource.remote.CommonRemote
import cat.bcn.commonmodule.model.Platform
import cat.bcn.commonmodule.model.Version
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import platform.UIKit.*

actual class OSAM constructor(
    private val vc: UIViewController
) {
    actual fun versionControl(
        appId: String,
        versionCode: Int,
        language: Language,
        f: (VersionControlResponse) -> Unit
    ) {
        try {
            GlobalScope.launch(Dispatchers.Main) {
                val version = CommonRemote().getVersion(
                    appId = appId,
                    versionCode = versionCode,
                    language = language,
                    platform = Platform.IOS
                )

                val alert = UIAlertController()

                alert.title = version.title(language)
                alert.message = version.message(language)

                alert.addAction(
                    UIAlertAction.actionWithTitle(
                        title = version.ok(language),
                        style = UIAlertActionStyleDefault,
                        handler = { f(VersionControlResponse.ACCEPTED) }
                    )
                )

                when (version.comparisonMode) {
                    Version.ComparisonMode.FORCE -> {
                        // Do nothing, by default will be not dismissable
                    }
                    else -> {
                        alert.addAction(UIAlertAction.actionWithTitle(
                            title = version.cancel(language),
                            style = UIAlertActionStyleCancel,
                            handler = { f(VersionControlResponse.CANCELLED) }
                        ))
                    }
                }

                vc.presentViewController(alert, animated = true, completion = null)
            }
        } catch (e: Exception) {
            f(VersionControlResponse.ERROR)
        }

    }

}
