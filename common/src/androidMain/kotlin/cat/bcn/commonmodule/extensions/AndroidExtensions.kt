package cat.bcn.commonmodule.extensions

import java.util.*

actual val isDebug = true // TODO BuildConfig.DEBUG
actual fun getCurrentDate(): Long = Calendar.getInstance().timeInMillis