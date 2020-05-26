package presentation.ui.app

import android.app.Application
import android.content.Context
import cat.bcn.commonmodule.data.datasource.local.*
import cat.bcn.commonmodule.data.datasource.remote.CommonRemoteDataSource
import cat.bcn.commonmodule.data.datasource.remote.RemoteDataSource
import cat.bcn.commonmodule.data.datasource.settings.Settings
import cat.bcn.commonmodule.data.repository.CommonRepository
import cat.bcn.commonmodule.data.repository.Repository
import cat.bcn.commonmodule.ui.error.ErrorHandler
import cat.bcn.commonmodule.ui.executor.Executor
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.generic.bind
import org.kodein.di.generic.instance
import org.kodein.di.generic.singleton

class App : Application(), KodeinAware {
    override val kodein by Kodein.lazy {
        import(appModule(this@App))
        import(domainModule)
        import(dataModule)
    }
}

fun appModule(context: Context) = Kodein.Module("appModule") {
    bind<Context>() with singleton { context }
    bind<Executor>() with singleton { Executor() }
    bind<ErrorHandler>() with singleton { ErrorHandler() }
}

val domainModule = Kodein.Module("domainModule") {

}

val dataModule = Kodein.Module("dataModule") {
    bind<Repository>() with singleton { CommonRepository(instance(), instance(), instance()) }
    bind<RemoteDataSource>() with singleton {
        CommonRemoteDataSource(instance(), endPoint = "")
    }
    bind<LocalDataSource>() with singleton { CommonLocalDataSource(DbDriver(instance())) }
    bind<Settings>() with singleton { Settings(context = instance()) }
    bind<Preferences>() with singleton { CommonPreferences(instance()) }
}
