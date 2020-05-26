package cat.bcn.commonmodule.data.repository

import cat.bcn.commonmodule.data.datasource.local.LocalDataSource
import cat.bcn.commonmodule.data.datasource.local.Preferences
import cat.bcn.commonmodule.data.datasource.remote.RemoteDataSource

class CommonRepository(
    private val remote: RemoteDataSource,
    private val local: LocalDataSource,
    private val preferences: Preferences
) : Repository
