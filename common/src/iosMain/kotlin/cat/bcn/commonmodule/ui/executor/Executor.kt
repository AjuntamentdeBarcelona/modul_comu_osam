package cat.bcn.commonmodule.ui.executor

import cat.bcn.commonmodule.testing.Mockable
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

@Mockable
internal actual class Executor {
    actual val main: CoroutineDispatcher = Dispatchers.Main
    actual val bg: CoroutineDispatcher = Dispatchers.Main // TODO change when using new Kotlin Native Memory Manager
}
