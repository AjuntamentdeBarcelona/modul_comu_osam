package cat.bcn.commonmodule.ui.executor

import kotlinx.coroutines.CoroutineDispatcher

internal expect class Executor {
    val main: CoroutineDispatcher
    val bg: CoroutineDispatcher
}
