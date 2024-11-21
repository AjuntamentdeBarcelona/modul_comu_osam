package cat.bcn.commonmodule.extensions

import platform.Foundation.NSDate
import platform.Foundation.timeIntervalSince1970
import kotlin.experimental.ExperimentalNativeApi

@OptIn(ExperimentalNativeApi::class)
actual val isDebug = Platform.isDebugBinary
actual fun getCurrentDate(): Long = NSDate().getMilliseconds()

fun NSDate.getMilliseconds(): Long = (this.timeIntervalSince1970 * 1000.0).toLong()