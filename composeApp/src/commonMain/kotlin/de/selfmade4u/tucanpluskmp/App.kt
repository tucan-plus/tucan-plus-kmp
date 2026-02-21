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
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import androidx.savedstate.serialization.SavedStateConfiguration
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
data object AfterLoginNavKey : NavKey

private val config = SavedStateConfiguration {
    serializersModule = SerializersModule {
        polymorphic(NavKey::class) {
            subclass(StartNavKey::class, StartNavKey.serializer())
            subclass(LoginNavKey::class, LoginNavKey.serializer())
            subclass(AfterLoginNavKey::class, AfterLoginNavKey.serializer())
        }
    }
}

@Composable
@Preview
fun App(uri: String? = null) {
    println("uri $uri")
    val backStack = rememberNavBackStack(config, LoginNavKey)
    val entryProvider = entryProvider {
        entry<StartNavKey> {
            Start(backStack)
        }
        entry<LoginNavKey> {
            BeforeLogin(backStack)
        }
        entry<AfterLoginNavKey> {
            AfterLogin(backStack)
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