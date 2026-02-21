package de.selfmade4u.tucanpluskmp

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey

class NavBackStackPreviewParameterProvider : PreviewParameterProvider<NavBackStack<NavKey>> {
    override val values: Sequence<NavBackStack<NavKey>> = sequenceOf(NavBackStack())
}

@Composable
@Preview
fun LoginForm(@PreviewParameter(NavBackStackPreviewParameterProvider::class) backStack: NavBackStack<NavKey>) {
    val snackbarHostState = remember { SnackbarHostState() }
    Scaffold(modifier = Modifier.fillMaxSize(), snackbarHost = {
        SnackbarHost(hostState = snackbarHostState)
    }) { innerPadding ->
        Column(
            Modifier
                .padding(innerPadding)
                .padding(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            //ShowLocalServices()
            //WifiDirect()
            //WifiDirectBonjour()
            Text("Logging in...")
        }
    }
}