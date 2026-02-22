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
import io.ktor.client.plugins.UserAgent
import io.ktor.client.plugins.logging.DEFAULT
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.plugins.logging.SIMPLE
import io.ktor.client.request.forms.submitForm
import io.ktor.client.request.parameter
import io.ktor.client.statement.bodyAsText
import io.ktor.client.statement.request
import io.ktor.http.Url
import io.ktor.http.parameters
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonNamingStrategy

@Serializable
data class TokenResponse(
                        @SerialName("id_token") val idToken: String,
                        @SerialName("access_token") val accessToken: String,
                        @SerialName("expires_in") val expiresIn: Int,
                        @SerialName("token_type") val tokenType: String,
                        @SerialName("refresh_token") val refreshToken: String,
                        val scope: String)

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
@Preview
fun AfterLogin(@PreviewParameter(NavBackStackPreviewParameterProvider::class) backStack: NavBackStack<NavKey>, uri: String = "") {
    val code = Url(uri).parameters["code"]!!
    println(code)
    val client = HttpClient() {
        install(UserAgent) {
            agent = "https://github.com/tucan-plus/tucan-plus-kmp Moritz.Hedtke@t-online.de"
        }
        install(Logging) {
            logger = Logger.SIMPLE
            level = LogLevel.ALL
        }
    }
    LaunchedEffect(Unit) {
        var response = client.submitForm(url = "https://dsf.tucan.tu-darmstadt.de/IdentityServer/connect/token",
            formParameters = parameters {
                append("client_id", "MobileApp")
                append("code", code)
                append("grant_type", "authorization_code")
                append("redirect_uri", "de.datenlotsen.campusnet.tuda:/oauth2redirect")
            }
        )
        println(response)
        val tokenResponse: TokenResponse = Json.decodeFromString(response.bodyAsText())
        println(tokenResponse)
        // now do the logincheck with that
        response = client.submitForm("https://www.tucan.tu-darmstadt.de/scripts/mgrqispi.dll", parameters {
            append("access_token", tokenResponse.accessToken)
            append("ARGUMENTS", "-N000000000000001,ids_mode")
            append("APPNAME", "CampusNet")
            append("PRGNAME", "LOGINCHECK")
            append("ids_mode", "M")
        })
        println(response)
        println(response.headers["REFRESH"])
        val body = response.bodyAsText()
        println(body)
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
        }
    }
}