package de.selfmade4u.tucanpluskmp

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.UriHandler
import androidx.datastore.core.DataStore
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import io.ktor.client.HttpClient
import io.ktor.http.Url
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import org.koin.core.KoinApplication
import org.koin.core.context.startKoin
import org.koin.core.module.Module
import org.koin.dsl.KoinAppDeclaration
import org.koin.dsl.includes

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform

@Composable
expect fun LoginHandler(backStack: NavBackStack<NavKey>)

expect suspend fun handleLogin(
    uri: Url,
    client: HttpClient,
    dataStore: DataStore<Settings?>,
    backStack: NavBackStack<NavKey>
);

@Composable
expect fun RequestNotificationPermission()

interface Notifier {
    fun sendNotification()
}

expect val platformModule: Module;

fun initKoin(config: KoinAppDeclaration? = null): KoinApplication {
    return startKoin {
        includes(config)
        modules(
            platformModule,
        )
    }
}

object FakeDataStore : DataStore<Settings?> {
    override val data: Flow<Settings?>
        get() = flow {
            emit(null)
        }

    override suspend fun updateData(transform: suspend (t: Settings?) -> Settings?): Settings? {
        TODO("Not yet implemented")
    }
}

