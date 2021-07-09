package cat.bcn.commonmodule.ui.executor

import kotlinx.coroutines.CoroutineDispatcher

expect class Executor {
    val main: CoroutineDispatcher
    val bg: CoroutineDispatcher
}
