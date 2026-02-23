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
    LaunchedEffect(Unit) {
        if (dataStore.data.first() == null) {
            backStack[backStack.size - 1] = LoginNavKey
        }
    }
   // val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    // TODO https://developer.android.com/reference/kotlin/androidx/compose/material3/package-summary all material expressive?
    DetailedDrawerExample(backStack, "TUCaN Plus") { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding),) {
            Text("Logged in: ${value != null}")
            Button(
                shapes = ButtonDefaults.shapes(),

                onClick = {
                    coroutineScope.launch {
                        dataStore.updateData { null }
                        backStack[backStack.size - 1] = LoginNavKey
                    }
                }) { Text("Logout") }
        }
    }
}