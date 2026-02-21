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
import androidx.compose.ui.unit.dp
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import io.ktor.client.HttpClient
import io.ktor.client.request.forms.submitForm
import io.ktor.client.request.parameter
import io.ktor.client.statement.request
import io.ktor.http.Url


@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
@Preview
fun AfterLogin(@PreviewParameter(NavBackStackPreviewParameterProvider::class) backStack: NavBackStack<NavKey>, uri: String = "") {
    val code = Url(uri).parameters["code"]!!
    println(code)
    val client = HttpClient()
    LaunchedEffect(Unit) {
        val response = client.submitForm("https://dsf.tucan.tu-darmstadt.de/IdentityServer/connect/token") {
            parameter("client_id", "MobileApp")
            parameter("code", code)
            parameter("grant_type", "authorization_code")
            parameter("redirect_uri", "de.datenlotsen.campusnet.tuda:/oauth2redirect")
        }
        println(response.request)
        println(response)
    }
    val snackbarHostState = remember { SnackbarHostState() }
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
            Text("Anmeldung wird durchgeführt...")
            Text(code)
        }
    }
}