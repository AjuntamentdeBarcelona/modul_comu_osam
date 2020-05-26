package presentation.ui.view.activity

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import cat.bcn.commonmodule.ui.presenter.Presenter
import com.app.app.R
import com.karumi.dexter.Dexter
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.single.PermissionListener
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.android.kodein
import org.kodein.di.android.subKodein
import presentation.ui.extension.hideMe
import presentation.ui.extension.showMe
import presentation.ui.extension.snackbar
import presentation.ui.extension.toast
import cat.bcn.commonmodule.ui.presenter.View as PresenterView

abstract class RootActivity<out V : PresenterView> : AppCompatActivity(), KodeinAware, PresenterView {

    private val progress: View by lazy { findViewById<View>(R.id.layoutProgress) }

    abstract val presenter: Presenter<V>

    abstract val layoutResourceId: Int

    abstract val activityModule: Kodein.Module

    override val kodein by subKodein(kodein()) {
        import(activityModule)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(layoutResourceId)

        initializeUI()
        registerListeners()

        presenter.attach()
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.detach()
    }

    abstract fun initializeUI()

    abstract fun registerListeners()

    override fun showError(error: String) = toast(error)

    override fun showProgress() = progress.showMe()

    override fun hideProgress() = progress.hideMe()

    override fun showRetry(error: String, f: () -> Unit) {
        val viewGroup = (this.findViewById<View>(android.R.id.content) as ViewGroup).getChildAt(0) as ViewGroup

        snackbar(viewGroup, message = error, retryCallback = { f() })
    }

    fun checkPermission(permission: String, onGranted: () -> Unit, onDenied: () -> Unit) {
        Dexter.withContext(this).withPermission(permission)
            .withListener(object : PermissionListener {
                override fun onPermissionGranted(p0: PermissionGrantedResponse?) {
                    onGranted()
                }

                override fun onPermissionRationaleShouldBeShown(p0: PermissionRequest?, p1: PermissionToken?) {
                    p1?.continuePermissionRequest()
                }

                override fun onPermissionDenied(p0: PermissionDeniedResponse?) {
                    onDenied()
                }

            })
            .check()
    }
}
