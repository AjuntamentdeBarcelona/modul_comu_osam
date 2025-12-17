package cat.bcn.commonmodule.ui.executor

import cat.bcn.commonmodule.testing.Mockable
import kotlinx.coroutines.CoroutineDispatcher

@Mockable
internal expect class Executor {
    val main: CoroutineDispatcher
    val bg: CoroutineDispatcher
}