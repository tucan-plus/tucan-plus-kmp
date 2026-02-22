package de.selfmade4u.tucanpluskmp.connector

import androidx.datastore.core.DataStore
import de.selfmade4u.tucanpluskmp.Localizer
import de.selfmade4u.tucanpluskmp.Settings
import de.selfmade4u.tucanpluskmp.TokenResponse
import io.ktor.client.HttpClient
import io.ktor.client.request.cookie
import io.ktor.client.request.get
import io.ktor.client.statement.HttpResponse
import kotlinx.coroutines.flow.first
import kotlinx.io.IOException
import kotlin.time.Clock

sealed class AuthenticatedHttpResponse<T> {
    data class Success<T>(var response: T) :
        AuthenticatedHttpResponse<T>()
    class NetworkLikelyTooSlow<T>() : AuthenticatedHttpResponse<T>()

    fun <O> map(): AuthenticatedResponse<O> {
        return when (this) {
            is Success<*> -> throw UnsupportedOperationException()
            is NetworkLikelyTooSlow<*> -> AuthenticatedResponse.NetworkLikelyTooSlow()
        }
    }
}

sealed class ParserResponse<T> {
    data class Success<T>(var response: T) :
        ParserResponse<T>()
    class SessionTimeout<T>() : ParserResponse<T>()

    /** Map errors to other type */
    fun <O> map(): AuthenticatedResponse<O> {
        return when (this) {
            is Success<*> -> throw UnsupportedOperationException()
            is SessionTimeout<*> -> AuthenticatedResponse.SessionTimeout()
        }
    }
}

sealed class AuthenticatedResponse<T> {
    data class Success<T>(var response: T) :
        AuthenticatedResponse<T>()

    class SessionTimeout<T>() : AuthenticatedResponse<T>()

    class InvalidCredentials<T>() : AuthenticatedResponse<T>()

    class TooManyAttempts<T>() : AuthenticatedResponse<T>()
    class NetworkLikelyTooSlow<T>() : AuthenticatedResponse<T>()

    fun <O> map(): AuthenticatedResponse<O> {
        return when (this) {
            is Success<*> -> throw UnsupportedOperationException()
            is SessionTimeout<*> -> SessionTimeout()
            is InvalidCredentials<*> -> InvalidCredentials()
            is TooManyAttempts<*> -> TooManyAttempts()
            is NetworkLikelyTooSlow<*> -> NetworkLikelyTooSlow()
        }
    }
}

suspend fun fetchAuthenticated(sessionCookie: String, url: String): AuthenticatedHttpResponse<HttpResponse> {
    val client = HttpClient()
    val r = try {
        client.get(url) {
            cookie("cnsc", sessionCookie)
        }
    } catch (e: IllegalStateException) {
        if (e.message?.contains("Content-Length mismatch") ?: true) {
            return AuthenticatedHttpResponse.NetworkLikelyTooSlow()
        }
        return AuthenticatedHttpResponse.NetworkLikelyTooSlow()
    } catch (e: IOException) {
        println("Request failed $e")
        return AuthenticatedHttpResponse.NetworkLikelyTooSlow()
    } catch (e: Throwable) {
        println("Request failed $e")
        return AuthenticatedHttpResponse.NetworkLikelyTooSlow()
    }
    return AuthenticatedHttpResponse.Success(r)
}

suspend fun <T> fetchAuthenticatedWithReauthentication(credentialSettingsDataStore: DataStore<Settings?>, url: (sessionId: String) -> String, parser: suspend (sessionId: String, menuLocalizer: Localizer, response: HttpResponse) -> ParserResponse<T>): AuthenticatedResponse<T> {
    val client = HttpClient()
    val settings = credentialSettingsDataStore.data.first()
    //if (Clock.System.now() < settings.lastRequestTime + 30*60*1000) {
        val response = fetchAuthenticated(
            settings!!.sessionCookie, url(settings.sessionId)
        )
        when (response) {
            is AuthenticatedHttpResponse.Success<HttpResponse> -> {
                when (val parserResponse = parser(settings.sessionId, settings.menuLocalizer, response.response)) {
                    is ParserResponse.Success<T> -> {
                        credentialSettingsDataStore.updateData { currentSettings ->
                            settings?.copy(lastRequestTime = Clock.System.now())
                        }
                        return AuthenticatedResponse.Success<T>(parserResponse.response)
                    }
                    is ParserResponse.SessionTimeout<*> -> {
                        // fall through
                    }
                }
            }
            is AuthenticatedHttpResponse.NetworkLikelyTooSlow<*> -> AuthenticatedResponse.NetworkLikelyTooSlow<T>()
        }
    //} else {
    //}
    /*val loginResponse: TucanLogin.LoginResponse
    try {
        loginResponse = TucanLogin.doLogin(
            client,
            settings.username,
            settings.password,
        )
    } catch (e: Throwable) {
        return AuthenticatedResponse.NetworkLikelyTooSlow<T>()
    }
    when (loginResponse) {
        is TucanLogin.LoginResponse.InvalidCredentials -> {
            // backStack[backStack.size - 1] = MainNavKey
            // TODO clear store
            return AuthenticatedResponse.InvalidCredentials()
        }
        is TucanLogin.LoginResponse.Success -> {
            settings = CredentialSettings(
                username = settings.username,
                password = settings.password,
                sessionId = loginResponse.sessionId,
                sessionCookie = loginResponse.sessionCookie,
                lastRequestTime = System.currentTimeMillis(),
                menuLocalizer = loginResponse.menuLocalizer
            )
            credentialSettingsDataStore.updateData { currentSettings ->
                OptionalCredentialSettings(
                    settings
                )
            }
            val response = fetchAuthenticated(
                loginResponse.sessionCookie, url(loginResponse.sessionId)
            )
            return when (response) {
                is AuthenticatedHttpResponse.Success<HttpResponse> -> {
                    when (val parserResponse = parser( loginResponse.sessionId, loginResponse.menuLocalizer, response.response)) {
                        is ParserResponse.Success<T> -> {
                            credentialSettingsDataStore.updateData { currentSettings ->
                                OptionalCredentialSettings(settings.copy(lastRequestTime = System.currentTimeMillis()))
                            }
                            return AuthenticatedResponse.Success<T>(parserResponse.response)
                        }
                        is ParserResponse.SessionTimeout<*> -> {
                            return AuthenticatedResponse.SessionTimeout() // should be unreachable
                        }
                    }
                }
                is AuthenticatedHttpResponse.NetworkLikelyTooSlow<*> -> AuthenticatedResponse.NetworkLikelyTooSlow<T>()
            }
        }
        is TucanLogin.LoginResponse.TooManyAttempts -> {
            // bad
            return AuthenticatedResponse.TooManyAttempts()
        }
    }*/
    return AuthenticatedResponse.TooManyAttempts()
}