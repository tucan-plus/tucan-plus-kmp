package de.selfmade4u.tucanpluskmp

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.LoadingIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey

class NavBackStackPreviewParameterProvider : PreviewParameterProvider<NavBackStack<NavKey>> {
    override val values: Sequence<NavBackStack<NavKey>> = sequenceOf(NavBackStack())
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
@Preview
fun BeforeLogin(@PreviewParameter(NavBackStackPreviewParameterProvider::class) backStack: NavBackStack<NavKey>) {
    val snackbarHostState = remember { SnackbarHostState() }
    val uriHandler = LocalUriHandler.current
    LaunchedEffect(Unit) {
        val result = getLoginUrl(uriHandler)
        backStack.add(AfterLoginNavKey)
    }
    Scaffold(modifier = Modifier.fillMaxSize(), snackbarHost = {
        SnackbarHost(hostState = snackbarHostState)
    }) { innerPadding ->
        Column(
            Modifier
                .padding(innerPadding).fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            LoadingIndicator()
            Text("Wartet auf Anmeldung im Browser")
        }
    }
}