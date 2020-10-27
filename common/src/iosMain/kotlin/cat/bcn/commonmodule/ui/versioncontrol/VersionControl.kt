package cat.bcn.commonmodule.ui.versioncontrol

import cat.bcn.commonmodule.data.datasource.remote.CommonRemote
import cat.bcn.commonmodule.model.App
import cat.bcn.commonmodule.model.Version
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import platform.UIKit.*

actual class VersionControl constructor(
    private val vc: UIViewController
) {
    actual fun check(
        appId: String,
        versionCode: Int,
        language: Language,
        f: (VersionControlResponse) -> Unit
    ) {
        GlobalScope.launch(Dispatchers.Main) {
            val version = CommonRemote().getVersion(
                appId = appId,
                versionCode = versionCode,
                language = language,
                platform = App.Platform.IOS
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
    }

}
