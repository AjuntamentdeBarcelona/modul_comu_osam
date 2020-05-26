package cat.bcn.commonmodule.ui.presenter

import cat.bcn.commonmodule.model.CommonError
import cat.bcn.commonmodule.model.Either
import cat.bcn.commonmodule.ui.error.ErrorHandler
import cat.bcn.commonmodule.ui.executor.Executor
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * Presenter
 */
abstract class Presenter<out V : View>(
    protected val errorHandler: ErrorHandler,
    protected val executor: Executor,
    val view: V
) {

    private val job = SupervisorJob()

    protected val scope = CoroutineScope(job + executor.main)

    protected fun onRetry(error: CommonError, retry: () -> Unit): Unit =
        view.showRetry(errorHandler.convert(error)) { retry() }

    protected fun onError(error: CommonError): Unit = view.showError(errorHandler.convert(error))

    abstract fun attach()

    fun detach() = job.cancel()

    fun CoroutineScope.ui(f: () -> Unit) {
        this.launch(executor.main) { f() }
    }

    protected suspend fun <T> execute(f: suspend () -> Either<CommonError, T>): Either<CommonError, T> =
        withContext(executor.bg) { f() }
}

interface View {
    fun showProgress()
    fun hideProgress()
    fun showRetry(error: String, f: () -> Unit)
    fun showError(error: String)
}
