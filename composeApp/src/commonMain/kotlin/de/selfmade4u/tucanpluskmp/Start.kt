package de.selfmade4u.tucanpluskmp

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.datastore.core.DataStore
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun Start(backStack: NavBackStack<NavKey>, dataStore: DataStore<Settings?>) {
    val value by dataStore.data.collectAsStateWithLifecycle(null)
    val coroutineScope = rememberCoroutineScope()
    DetailedDrawerExample(backStack, "TUCaN Plus") { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding)) {
            if (value != null) {
                Button(
                    shapes = ButtonDefaults.shapes(),

                    onClick = {
                        coroutineScope.launch {
                            dataStore.updateData { null }
                        }
                    }) { Text("Logout") }
            } else {
                Button(
                    shapes = ButtonDefaults.shapes(),

                    onClick = {
                        coroutineScope.launch {
                            backStack[backStack.size - 1] = LoginNavKey(
                            "https://dsf.tucan.tu-darmstadt.de/IdentityServer/connect/authorize?client_id=MobileApp&scope=openid+DSF+profile+offline_access&response_mode=query&response_type=code&ui_locales=de&redirect_uri=de.datenlotsen.campusnet.tuda:/oauth2redirect"
                            )
                        }
                    }) { Text("Login") }
                Button(
                    shapes = ButtonDefaults.shapes(),

                    onClick = {
                        coroutineScope.launch {
                            backStack[backStack.size - 1] = LoginNavKey("https://dsf.tucan.tu-darmstadt.localhost:8443/IdentityServer/connect/authorize?client_id=MobileApp&scope=openid+DSF+profile+offline_access&response_mode=query&response_type=code&ui_locales=de&redirect_uri=de.datenlotsen.campusnet.tuda:/oauth2redirect")
                        }
                    }) { Text("Login as Tester") }
            }
            RequestNotificationPermission()
        }
    }
}