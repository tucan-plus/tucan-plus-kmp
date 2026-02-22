package de.selfmade4u.tucanpluskmp

import androidx.compose.ui.platform.UriHandler
import androidx.datastore.core.DataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform

expect suspend fun getLoginUrl(uriHandler: UriHandler): String

object FakeDataStore : DataStore<Settings?> {
    override val data: Flow<Settings?>
        get() = flow {
            emit(null)
        }

    override suspend fun updateData(transform: suspend (t: Settings?) -> Settings?): Settings? {
        TODO("Not yet implemented")
    }
}