package cat.bcn.commonmodule.data.datasource.local

import com.squareup.sqldelight.db.SqlDriver

expect class DbDriver {
    fun get(): SqlDriver
}

class CommonLocalDataSource(dbDriver: DbDriver) : LocalDataSource
