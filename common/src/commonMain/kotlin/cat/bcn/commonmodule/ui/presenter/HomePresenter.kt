package cat.bcn.commonmodule.ui.presenter

import cat.bcn.commonmodule.data.datasource.local.Preferences
import cat.bcn.commonmodule.data.repository.Repository
import cat.bcn.commonmodule.ui.error.ErrorHandler
import cat.bcn.commonmodule.ui.executor.Executor

class HomePresenter(
    private val preferences: Preferences,
    private val repository: Repository,
    errorHandler: ErrorHandler,
    executor: Executor,
    view: HomeView
) : Presenter<HomeView>(errorHandler, executor, view) {

    override fun attach() {

    }


}

interface HomeView : View {
}
