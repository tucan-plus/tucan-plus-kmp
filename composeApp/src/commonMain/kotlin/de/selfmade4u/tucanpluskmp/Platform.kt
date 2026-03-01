package de.selfmade4u.tucanpluskmp

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.UriHandler
import androidx.datastore.core.DataStore
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform

@Composable
expect fun LoginHandler(backStack: NavBackStack<NavKey>)

object FakeDataStore : DataStore<Settings?> {
    override val data: Flow<Settings?>
        get() = flow {
            emit(null)
        }

    override suspend fun updateData(transform: suspend (t: Settings?) -> Settings?): Settings? {
        TODO("Not yet implemented")
    }
}