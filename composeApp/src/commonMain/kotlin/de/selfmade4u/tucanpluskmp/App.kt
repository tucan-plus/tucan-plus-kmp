package de.selfmade4u.tucanpluskmp

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialExpressiveTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.material3.pulltorefresh.PullToRefreshDefaults
import androidx.compose.material3.pulltorefresh.PullToRefreshState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.datastore.core.DataStore
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import androidx.savedstate.serialization.SavedStateConfiguration
import de.selfmade4u.tucanpluskmp.database.ModuleResults
import de.selfmade4u.tucanpluskmp.destination.ModuleResultsComposable
import de.selfmade4u.tucanpluskmp.destination.MyExamsComposable
import org.jetbrains.compose.resources.painterResource
import kotlinx.serialization.Serializable
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic

import tucanpluskmp.composeapp.generated.resources.Res

@Serializable
data object StartNavKey : NavKey

@Serializable
data object LoginNavKey : NavKey

@Serializable
data class AfterLoginNavKey(val uri: String) : NavKey

@Serializable
data object ModuleResultsKey : NavKey

@Serializable
data object MyExamsKey : NavKey

private val config = SavedStateConfiguration {
    serializersModule = SerializersModule {
        polymorphic(NavKey::class) {
            subclass(StartNavKey::class, StartNavKey.serializer())
            subclass(LoginNavKey::class, LoginNavKey.serializer())
            subclass(AfterLoginNavKey::class, AfterLoginNavKey.serializer())
            subclass(ModuleResultsKey::class, ModuleResultsKey.serializer())
            subclass(MyExamsKey::class, MyExamsKey.serializer())
        }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun App(uri: String?, dataStore: DataStore<Settings?> = FakeDataStore, database: AppDatabase) {
    println("uri $uri")
    val initialNav = if (uri != null && uri.startsWith("de.datenlotsen.campusnet.tuda:/oauth2redirect?")) {
        AfterLoginNavKey(uri)
    } else if (uri != null && uri.contains("APPNAME")) {
        AfterLoginNavKey(uri)
    } else {
        StartNavKey
    }
    val backStack = rememberNavBackStack(config, initialNav)
    val entryProvider = entryProvider {
        entry<StartNavKey> {
            Start(backStack, dataStore)
        }
        entry<LoginNavKey> {
            BeforeLogin(backStack)
        }
        entry<AfterLoginNavKey> { key ->
            AfterLogin(backStack, dataStore, key.uri)
        }
        entry<ModuleResultsKey> { key ->
            ModuleResultsComposable(backStack, dataStore, database)
        }
        entry<MyExamsKey> { key ->
            MyExamsComposable(backStack, dataStore, database)
        }
    }
    MaterialExpressiveTheme(colorScheme = if (isSystemInDarkTheme()) {
        darkColorScheme()
    } else {
        lightColorScheme()
    }) {
        NavDisplay(
            backStack = backStack,
            onBack = { backStack.removeLastOrNull() },
            entryProvider = entryProvider
        )
    }
}


@Composable
@OptIn(ExperimentalMaterial3ExpressiveApi::class)
fun BoxScope.MyLoadingIndicator(
    state: PullToRefreshState,
    isRefreshing: Boolean
) {
    PullToRefreshDefaults.LoadingIndicator(
        state = state,
        isRefreshing = isRefreshing,
        modifier = Modifier
            .align(Alignment.TopCenter)
            .semantics {
                contentDescription = if (isRefreshing) {
                    "Refreshing"
                } else {
                    "Not Refreshing"
                }
            },
    )
}

