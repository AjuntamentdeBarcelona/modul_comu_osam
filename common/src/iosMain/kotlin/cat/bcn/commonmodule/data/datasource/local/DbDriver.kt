package cat.bcn.commonmodule.data.datasource.local

import com.squareup.sqldelight.db.SqlDriver
import com.squareup.sqldelight.drivers.native.NativeSqliteDriver
import com.tempos21.eroskiclub.db.eroskiclub

actual class DbDriver {
    actual fun get(): SqlDriver = NativeSqliteDriver(eroskiclub.Schema, "db")
}
