package de.selfmade4u.tucanpluskmp.connector


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