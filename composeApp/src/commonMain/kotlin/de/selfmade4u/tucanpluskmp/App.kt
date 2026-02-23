package de.selfmade4u.tucanpluskmp

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.datastore.core.DataStore
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import androidx.savedstate.serialization.SavedStateConfiguration
import de.selfmade4u.tucanpluskmp.database.ModuleResults
import de.selfmade4u.tucanpluskmp.destination.ModuleResultsComposable
import org.jetbrains.compose.resources.painterResource
import kotlinx.serialization.Serializable
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic

import tucanpluskmp.composeapp.generated.resources.Res
import tucanpluskmp.composeapp.generated.resources.compose_multiplatform

@Serializable
data object StartNavKey : NavKey

@Serializable
data object LoginNavKey : NavKey

@Serializable
data class AfterLoginNavKey(val uri: String) : NavKey

@Serializable
data object ModuleResultsKey : NavKey

private val config = SavedStateConfiguration {
    serializersModule = SerializersModule {
        polymorphic(NavKey::class) {
            subclass(StartNavKey::class, StartNavKey.serializer())
            subclass(LoginNavKey::class, LoginNavKey.serializer())
            subclass(AfterLoginNavKey::class, AfterLoginNavKey.serializer())
            subclass(ModuleResultsKey::class, ModuleResultsKey.serializer())
        }
    }
}

@Composable
fun App(uri: String?, dataStore: DataStore<Settings?> = FakeDataStore, database: AppDatabase) {
    println("uri $uri")
    val initialNav = if (uri != null && uri.startsWith("de.datenlotsen.campusnet.tuda:/oauth2redirect?")) {
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
    }
    NavDisplay(
        backStack = backStack,
        onBack = { backStack.removeLastOrNull() },
        entryProvider = entryProvider
    )
}

@Composable
@Preview
fun Login() {
    MaterialTheme {
        var showContent by remember { mutableStateOf(false) }
        Column(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.primaryContainer)
                .safeContentPadding()
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Button(onClick = {
                showContent = !showContent
            }) {
                Text("Click me!")
            }
            AnimatedVisibility(showContent) {
                val greeting = remember { Greeting().greet() }
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Image(painterResource(Res.drawable.compose_multiplatform), null)
                    Text("Compose: $greeting")
                }
            }
        }
    }
}