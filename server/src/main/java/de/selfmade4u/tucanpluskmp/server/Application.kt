package de.selfmade4u.tucanpluskmp.server

import de.selfmade4u.tucanpluskmp.TokenResponse
import io.ktor.network.tls.certificates.*
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.http.content.staticResources
import io.ktor.server.jetty.jakarta.Jetty
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.sessions.SessionTransportTransformerEncrypt
import io.ktor.server.sessions.Sessions
import io.ktor.server.sessions.cookie
import io.ktor.server.sessions.directorySessionStorage
import io.ktor.server.sessions.sessions
import io.ktor.server.sessions.set
import io.ktor.util.hex
import kotlinx.serialization.Serializable
import org.slf4j.*
import java.io.*

@Serializable
data class UserSession(val id: String, val count: Int)

// http://localhost:8080/
// adb reverse tcp:8080 tcp:8080
fun main() {
    val appProperties = serverConfig {
        watchPaths = listOf("resources")
        developmentMode = true
        module {
            install(ContentNegotiation) {
                json()
            }
            install(Sessions) {
                cookie<UserSession>("cnsc", directorySessionStorage(File("build/.sessions"))) {
                    transform(SessionTransportTransformerEncrypt(hex("00112233445566778899aabbccddeeff"), hex("00112233445566778899aabbccddeeff")))
                }
            }
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
            domains = listOf("localhost")
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
            call.respondRedirect("de.datenlotsen.campusnet.tuda:/oauth2redirect?code=test&uri=http%3A%2F%2Flocalhost%3A8080%2FIdentityServer%2Fconnect%2Ftoken")
        }
        post("/IdentityServer/connect/token") {
            // TODO return token
            call.respond(TokenResponse("", "", 0, "", "", "", "http://localhost:8080/scripts/mgrqispi.dll"))
        }
        post("/scripts/mgrqispi.dll") {
            // read entry
            call.sessions.set(UserSession(id = "123abc", count = 0))
            call.response.header("REFRESH", "0; URL=/scripts/mgrqispi.dll?APPNAME=CampusNet&PRGNAME=STARTPAGE_DISPATCH&ARGUMENTS=-N1337,-N000019,-N000000000000000")
            call.respond("success")
        }
        get("/scripts/mgrqispi.dll") {
            println("get data")
        }
        staticResources("/IdentityServer/connect/authorize", "login")
    }
}