package cat.bcn.commonmodule.data.datasource.local

import android.content.Context
import com.squareup.sqldelight.android.AndroidSqliteDriver
import com.squareup.sqldelight.db.SqlDriver
import com.tempos21.eroskiclub.db.eroskiclub

actual class DbDriver(private val context: Context) {
    actual fun get(): SqlDriver = AndroidSqliteDriver(
        schema = eroskiclub.Schema,
        context = context,
        name = "db"
    )
}
