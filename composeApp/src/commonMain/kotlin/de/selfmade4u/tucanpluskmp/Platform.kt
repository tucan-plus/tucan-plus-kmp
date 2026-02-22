package de.selfmade4u.tucanpluskmp

import androidx.compose.ui.platform.UriHandler
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import kotlinx.coroutines.flow.Flow

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform

expect suspend fun getLoginUrl(uriHandler: UriHandler): String

object FakeDataStore : DataStore<TokenResponse> {
    override val data: Flow<TokenResponse>
        get() = TODO("Not yet implemented")

    override suspend fun updateData(transform: suspend (t: TokenResponse) -> TokenResponse): TokenResponse {
        TODO("Not yet implemented")
    }
}