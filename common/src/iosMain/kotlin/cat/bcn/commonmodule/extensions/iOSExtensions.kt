package cat.bcn.commonmodule.extensions

import platform.Foundation.NSDate
import platform.Foundation.timeIntervalSince1970

actual fun getCurrentDate(): Long = NSDate().getMilliseconds()

fun NSDate.getMilliseconds(): Long = (this.timeIntervalSince1970 * 1000.0).toLong()