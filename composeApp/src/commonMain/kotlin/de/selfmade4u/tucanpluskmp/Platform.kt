package de.selfmade4u.tucanpluskmp

import androidx.compose.ui.platform.UriHandler
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform

expect suspend fun getLoginUrl(uriHandler: UriHandler): String

object FakeDataStore : DataStore<TokenResponse?> {
    override val data: Flow<TokenResponse?>
        get() = flow {
            emit(null)
        }

    override suspend fun updateData(transform: suspend (t: TokenResponse?) -> TokenResponse?): TokenResponse? {
        TODO("Not yet implemented")
    }
}