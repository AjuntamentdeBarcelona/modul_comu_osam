package presentation.ui.view.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import cat.bcn.commonmodule.ui.presenter.Presenter
import kotlinx.android.synthetic.main.view_progress.*
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.android.subKodein
import org.kodein.di.android.x.kodein
import presentation.ui.extension.hideMe
import presentation.ui.extension.showMe
import presentation.ui.extension.snackbar
import cat.bcn.commonmodule.ui.presenter.View as PresenterView

/**
 * RootFragment
 */
abstract class RootFragment<out V : PresenterView> : Fragment(), KodeinAware, PresenterView {

    private val progress: View by lazy { layoutProgress }

    abstract val presenter: Presenter<V>

    abstract val layoutResourceId: Int

    abstract val fragmentModule: Kodein.Module

    override val kodein by subKodein(kodein()) {
        import(fragmentModule)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initializeUI()
        registerListeners()

        presenter.attach()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        inflater.inflate(layoutResourceId, container, false)

    override fun onDestroy() {
        super.onDestroy()
        presenter.detach()
    }

    abstract fun initializeUI()

    abstract fun registerListeners()

    override fun showProgress() = progress.showMe()

    override fun hideProgress() = progress.hideMe()

    override fun showRetry(error: String, f: () -> Unit) {
        view?.let { activity?.snackbar(it, message = error, retryCallback = { f() }) }
    }

    override fun showError(error: String) {
        view?.let { activity?.snackbar(it, message = error, showAction = false) }
    }
}
