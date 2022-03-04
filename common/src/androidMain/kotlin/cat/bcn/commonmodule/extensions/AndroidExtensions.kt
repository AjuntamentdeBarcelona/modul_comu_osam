package cat.bcn.commonmodule.extensions

import cat.bcn.commonmodule.BuildConfig
import java.util.*

actual val isDebug = BuildConfig.DEBUG
actual fun getCurrentDate(): Long = Calendar.getInstance().timeInMillis