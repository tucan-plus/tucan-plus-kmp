package de.selfmade4u.tucanpluskmp.connector

import androidx.datastore.core.DataStore
import com.fleeksoft.ksoup.Ksoup
import de.selfmade4u.tucanpluskmp.Localizer
import de.selfmade4u.tucanpluskmp.Root
import de.selfmade4u.tucanpluskmp.Settings
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow

object ExamResultsConnector : Connector<String?, Any> {
    override suspend fun getUncached(
        credentialSettingsDataStore: DataStore<Settings?>,
        input: String?
    ): AuthenticatedResponse<Any> {
        TODO("Not yet implemented")
    }

    override suspend fun parseHttpResponse(
        menuId: String,
        sessionId: String,
        menuLocalizer: Localizer,
        response: HttpResponse
    ): ParserResponse<Any> {
        TODO("Not yet implemented")
    }

    override fun Root.parse(
        menuId: String,
        sessionId: String,
        menuLocalizer: Localizer
    ): ParserResponse<Any> {
        TODO("Not yet implemented")
    }

    override fun extractRelevantPages(credentialSettingsDataStore: DataStore<Settings?>): Flow<String?> = flow {
        val credentials = credentialSettingsDataStore.data.first()!!
        val response = fetchAuthenticated(
            credentials.sessionCookie, "https://www.tucan.tu-darmstadt.de/scripts/mgrqispi.dll?APPNAME=CampusNet&PRGNAME=EXAMRESULTS&ARGUMENTS=-N${credentials.sessionId},-N000325,"
        ) as AuthenticatedHttpResponse.Success
        val document = Ksoup.parse(response.response.bodyAsText())
        val options = document.getElementsByTag("option")
        options.forEach { e -> emit(e.value()) }
    }
}