package de.selfmade4u.tucanpluskmp

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun Start(backStack: NavBackStack<NavKey>) {
   // val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    // TODO https://developer.android.com/reference/kotlin/androidx/compose/material3/package-summary all material expressive?
    DetailedDrawerExample(backStack, "TUCaN Plus") { innerPadding ->
        Button(
            shapes = ButtonDefaults.shapes(),
            modifier = Modifier.padding(innerPadding),
            onClick = {
                coroutineScope.launch {
                    backStack[backStack.size - 1] = LoginNavKey
                }
            }) { Text("Logout") }
    }
}