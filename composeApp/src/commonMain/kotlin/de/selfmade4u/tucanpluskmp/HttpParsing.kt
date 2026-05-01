package de.selfmade4u.tucanpluskmp

import com.fleeksoft.ksoup.Ksoup
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpStatusCode
import okio.ByteString.Companion.encodeUtf8
import kotlin.uuid.ExperimentalUuidApi

fun String.hashedWithSha256(): String =
    encodeUtf8().sha256().hex()

class Response(
    val response: HttpResponse,
    var headers: MutableMap<String, List<String>>,
    var checkedStatus: Boolean = false
) {
    fun status(status: HttpStatusCode) {
        check(response.status == status) { "actual   ${response.status} expected $status" }
        checkedStatus = true
    }

    fun maybeHeader(key: String, values: List<String>) {
        check(checkedStatus) { "you need to check the status before checking the headers" }
        val actualValue = headers.remove(key)
        check(actualValue == null || actualValue == values) { "actual   $actualValue\nexpected $values" }
    }

    fun header(key: String, values: List<String>) {
        check(checkedStatus) { "you need to check the status before checking the headers" }
        val actualValue = headers.remove(key)
        check(actualValue == values) { "actual   $actualValue\nexpected $values\nremaining $headers" }
    }

    fun header(key: String, values: String) {
        header(key, listOf(values))
    }

    fun ignoreHeader(key: String) {
        check(checkedStatus) { "you need to check the status before checking the headers" }
        val actualValue = headers.remove(key)
        check(actualValue != null) { "expected header with key $key\nremaining $headers" }
    }

    fun maybeIgnoreHeader(key: String) {
        check(checkedStatus) { "you need to check the status before checking the headers" }
        headers.remove(key)
    }

    fun hasHeader(key: String): Boolean {
        check(checkedStatus) { "you need to check the status before checking the headers" }
        return headers.containsKey(key)
    }

    fun extractHeader(key: String): List<String> {
        check(checkedStatus) { "you need to check the status before checking the headers" }
        return headers.remove(key)!!
    }

    suspend fun <T> root(init: Root.() -> T): T {
        check(headers.isEmpty()) { "unparsed headers $headers" }
        val document = Ksoup.parse(response.bodyAsText())
        //println(document)
        check(document.nameIs("#root")) { document.normalName() }
        check(document.attributesSize() == 0) { document.attributes() }
        val node = RootImpl(
            document,
            document.childNodes()
                .filterNot(::shouldIgnore)
                .toMutableList()
        )
        return node.init()
    }

}

@OptIn(ExperimentalUuidApi::class)
suspend fun <T> response(
    response: HttpResponse,
    init: suspend Response.() -> T
): T {
    /*val db = context?.let {
        MyDatabase.getDatabase(context)
    }*/
    try {
        val result = Response(response, response.headers.entries().groupBy({ it.key.lowercase() }, { it.value }).mapValues { (_, values) -> values.flatten() }.toMutableMap()).init()
        /*db?.cacheDao()?.insertAll(
            CacheEntry(
                0,
                response.request.url.toString(),
                response.request.url.toString(), // TODO FIXME at least remove session id
                response.bodyAsText(),
                LocalDateTime.now(Clock.systemUTC()),
                null
            )
        )*/
        return result
    } catch (e: IllegalStateException) {
        // cd app/src/test/resources
        // adb pull /data/data/de.selfmade4u.tucanplus/files/parsingerrors/
        /*if (context != null) {
            val dir = File(context.filesDir, "parsingerrors")
            dir.mkdirs()
            val fileOutputStream = FileOutputStream(File(dir, "error${Uuid.random()}.html"))
            fileOutputStream.use {
                it.write(response.bodyAsText().toByteArray())
            }
            db?.cacheDao()?.insertAll(
                CacheEntry(
                    0,
                    response.request.url.toString(),
                    response.request.url.toString(),
                    response.bodyAsText(),
                    LocalDateTime.now(Clock.systemUTC()),
                    e.toString(),
                )
            )
        }*/
        throw e
    }
}
