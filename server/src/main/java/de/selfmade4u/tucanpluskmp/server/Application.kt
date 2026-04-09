package de.selfmade4u.tucanpluskmp.server

import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.network.tls.certificates.*
import io.ktor.server.application.*
import io.ktor.server.auth.Authentication
import io.ktor.server.auth.AuthenticationStrategy
import io.ktor.server.auth.UserIdPrincipal
import io.ktor.server.auth.authenticate
import io.ktor.server.auth.form
import io.ktor.server.auth.principal
import io.ktor.server.engine.*
import io.ktor.server.http.content.staticResources
import io.ktor.server.jetty.jakarta.Jetty
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.slf4j.*
import java.io.*
import java.security.KeyStore

// https://localhost:8443/
// adb reverse tcp:8443 tcp:8443
fun main() {
    val appProperties = serverConfig {
        watchPaths = listOf("resources")
        developmentMode = true
        module {
            module()
        }
    }
    embeddedServer(Jetty, appProperties) {
        envConfig()

    }.start(true)
}

private fun ApplicationEngine.Configuration.envConfig() {

    val keyStoreFile = File("build/keystore.jks")
    val keyStore = buildKeyStore {
        certificate("sampleAlias") {
            password = "foobar"
            domains = listOf("dsf.tucan.tu-darmstadt.localhost")
        }
    }
    keyStore.saveToFile(keyStoreFile, "123456")

    connector {
        port = 8080
    }
    sslConnector(
        keyStore = keyStore,
        keyAlias = "sampleAlias",
        keyStorePassword = { "123456".toCharArray() },
        privateKeyPassword = { "foobar".toCharArray() }) {
        port = 8443
        keyStorePath = keyStoreFile
    }
}

fun Application.module() {
    routing {
        post("/IdentityServer/connect/authorize") {
            println("TODO validate password")
            // TODO validate password and then redirect
            call.respondRedirect("de.datenlotsen.campusnet.tuda:/oauth2redirect?code=test&uri=https%3A%2F%2Fdsf.tucan.tu-darmstadt.localhost%3A8443%2FIdentityServer%2Fconnect%2Ftoken")
        }
        staticResources("/IdentityServer/connect/authorize", "login")
    }
}