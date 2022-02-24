package cat.bcn.commonmodule.extensions

import java.util.*

actual fun getCurrentDate(): Long = Calendar.getInstance().timeInMillis