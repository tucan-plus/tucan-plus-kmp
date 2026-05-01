@file:OptIn(ExperimentalContracts::class)
@file:Suppress("LEAKED_IN_PLACE_LAMBDA", "WRONG_INVOCATION_KIND")

package de.selfmade4u.tucanpluskmp

import com.fleeksoft.ksoup.nodes.Attribute
import com.fleeksoft.ksoup.nodes.Node
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

// https://kotlinlang.org/docs/type-safe-builders.html#how-it-works
// https://kotlinlang.org/docs/ksp-overview.html

// https://github.com/fleeksoft/ksoup
// https://github.com/skrapeit/skrape.it - no multiplatform yet
// https://github.com/MohamedRejeb/Ksoup https://github.com/MohamedRejeb/Ksoup/issues/26 seems like it only has a event based api

annotation class HtmlFromResources(val path: String)
@DslMarker
annotation class HtmlTagMarker

interface HtmlTag {
    fun attribute(key: String, value: String?)
    fun attributeValue(key: String): String
    fun extractText(): String
    fun text(text: String)
    fun dataHash(hash: String)
    fun extractData(): String
    // this misses it's contract function now
    fun <C, R> initTag(
        tag: String,
        createTag: (node: Node, iterator: MutableList<Node>, attributes: MutableList<Attribute>) -> C,
        init:  C.() -> R
    ): R
    // shit here we expose ksoup again
    fun peek(): Node?
    fun peekAttribute(): Attribute?
}
interface Root : HtmlTag {
    fun <T> doctypeImpl(init: Doctype.() -> T): T
    fun <R> htmlImpl(init: Html.() -> R): R
}

interface Doctype : HtmlTag

interface Html : HtmlTag {
    fun <R> headImpl(init: Head.() -> R): R
    fun <R> bodyImpl(init: Body.() -> R): R
}

interface Head : HtmlTag {
    fun <R> titleImpl(init: Title.() -> R): R
    fun <R> metaImpl(init: Meta.() -> R): R
    fun <R> linkImpl(init: Link.() -> R): R
    fun <R> styleImpl(init: Head.() -> R): R
    fun <R> scriptImpl(init: Script.() -> R): R
}

sealed interface Body : HtmlTag {
    fun <R> scriptImpl(init: Script.() -> R): R
    fun <R> styleImpl(init: Body.() -> R): R
    fun <R> aImpl(init: Body.() -> R): R
    fun <R> divImpl(init: Body.() -> R): R
    fun <R> formImpl(init: Body.() -> R): R
    fun <R> fieldsetImpl(init: Body.() -> R): R
    fun <R> imgImpl(init: Body.() -> R): R
    fun <R> legendImpl(init: Body.() -> R): R
    fun <R> labelImpl(init: Body.() -> R): R
    fun <R> h1Impl(init: Body.() -> R): R
    fun <R> pImpl(init: Body.() -> R): R
    fun <R> ulImpl(init: Body.() -> R): R
    fun <R> liImpl(init: Body.() -> R): R
    fun <R> headerImpl(init: Body.() -> R): R
    fun <R> spanImpl(init: Body.() -> R): R
    fun <R> bImpl(init: Body.() -> R): R
    fun <R> brImpl(init: Body.() -> R): R
    fun <R> optionImpl(init: Body.() -> R): R
    fun <R> inputImpl(init: Body.() -> R): R
    fun <R> selectImpl(init: Body.() -> R): R
    fun <R> tableImpl(init: Body.() -> R): R
    fun <R> theadImpl(init: Body.() -> R): R
    fun <R> tbodyImpl(init: Body.() -> R): R
    fun <R> trImpl(init: Body.() -> R): R
    fun <R> tdImpl(init: Body.() -> R): R
    fun <R> thImpl(init: Body.() -> R): R
}

interface Title : HtmlTag
interface Meta : HtmlTag
interface Link : HtmlTag
interface Script : HtmlTag

// https://github.com/Kotlin/kotlinx.html/blob/76b16f09180185a9e283e164fa02fb54a1627e9f/src/commonMain/kotlin/generated/gen-tags-d.kt#L24-L25
fun <T> Root.doctype(init: Doctype.() -> T): T {
    contract { callsInPlace(init, InvocationKind.EXACTLY_ONCE) }
    return doctypeImpl(init)
}

fun <R> Root.html(init: Html.() -> R): R {
    contract { callsInPlace(init, InvocationKind.EXACTLY_ONCE) }
    return htmlImpl(init)
}

fun <R> Html.head(init: Head.() -> R): R {
    contract { callsInPlace(init, InvocationKind.EXACTLY_ONCE) }
    return headImpl(init)
}

fun <R> Html.body(init: Body.() -> R): R {
    contract { callsInPlace(init, InvocationKind.EXACTLY_ONCE) }
    return bodyImpl(init)
}
fun <R> Head.title(init: Title.() -> R): R {
    contract { callsInPlace(init, InvocationKind.EXACTLY_ONCE) }
    return titleImpl(init)
}

fun <R> Head.meta(init: Meta.() -> R): R {
    contract { callsInPlace(init, InvocationKind.EXACTLY_ONCE) }
    return metaImpl(init)
}

fun <R> Head.link(init: Link.() -> R): R {
    contract { callsInPlace(init, InvocationKind.EXACTLY_ONCE) }
    return linkImpl(init)
}

fun <R> Head.style(init: Head.() -> R): R {
    contract { callsInPlace(init, InvocationKind.EXACTLY_ONCE) }
    return styleImpl(init)
}

fun <R> Head.script(init: Script.() -> R): R {
    contract { callsInPlace(init, InvocationKind.EXACTLY_ONCE) }
    return scriptImpl(init)
}
fun <R> Body.script(init: Script.() -> R): R {
    contract { callsInPlace(init, InvocationKind.EXACTLY_ONCE) }
    return scriptImpl(init)
}

fun <R> Body.style(init: Body.() -> R): R {
    contract { callsInPlace(init, InvocationKind.EXACTLY_ONCE) }
    return styleImpl(init)
}

fun <R> Body.a(init: Body.() -> R): R {
    contract { callsInPlace(init, InvocationKind.EXACTLY_ONCE) }
    return aImpl(init)
}

fun <R> Body.div(init: Body.() -> R): R {
    contract { callsInPlace(init, InvocationKind.EXACTLY_ONCE) }
    return divImpl(init)
}

fun <R> Body.form(init: Body.() -> R): R {
    contract { callsInPlace(init, InvocationKind.EXACTLY_ONCE) }
    return formImpl(init)
}

fun <R> Body.fieldset(init: Body.() -> R): R {
    contract { callsInPlace(init, InvocationKind.EXACTLY_ONCE) }
    return fieldsetImpl(init)
}

fun <R> Body.img(init: Body.() -> R): R {
    contract { callsInPlace(init, InvocationKind.EXACTLY_ONCE) }
    return imgImpl(init)
}

fun <R> Body.legend(init: Body.() -> R): R {
    contract { callsInPlace(init, InvocationKind.EXACTLY_ONCE) }
    return legendImpl(init)
}

fun <R> Body.label(init: Body.() -> R): R {
    contract { callsInPlace(init, InvocationKind.EXACTLY_ONCE) }
    return labelImpl(init)
}

fun <R> Body.h1(init: Body.() -> R): R {
    contract { callsInPlace(init, InvocationKind.EXACTLY_ONCE) }
    return h1Impl(init)
}

fun <R> Body.p(init: Body.() -> R): R {
    contract { callsInPlace(init, InvocationKind.EXACTLY_ONCE) }
    return pImpl(init)
}

fun <R> Body.ul(init: Body.() -> R): R {
    contract { callsInPlace(init, InvocationKind.EXACTLY_ONCE) }
    return ulImpl(init)
}

fun <R> Body.li(init: Body.() -> R): R {
    contract { callsInPlace(init, InvocationKind.EXACTLY_ONCE) }
    return liImpl(init)
}

fun <R> Body.header(init: Body.() -> R): R {
    contract { callsInPlace(init, InvocationKind.EXACTLY_ONCE) }
    return headerImpl(init)
}

fun <R> Body.span(init: Body.() -> R): R {
    contract { callsInPlace(init, InvocationKind.EXACTLY_ONCE) }
    return spanImpl(init)
}

fun <R> Body.b(init: Body.() -> R): R {
    contract { callsInPlace(init, InvocationKind.EXACTLY_ONCE) }
    return bImpl(init)
}

fun <R> Body.br(init: Body.() -> R): R {
    contract { callsInPlace(init, InvocationKind.EXACTLY_ONCE) }
    return brImpl(init)
}

fun <R> Body.option(init: Body.() -> R): R {
    contract { callsInPlace(init, InvocationKind.EXACTLY_ONCE) }
    return optionImpl(init)
}

fun <R> Body.input(init: Body.() -> R): R {
    contract { callsInPlace(init, InvocationKind.EXACTLY_ONCE) }
    return inputImpl(init)
}

fun <R> Body.select(init: Body.() -> R): R {
    contract { callsInPlace(init, InvocationKind.EXACTLY_ONCE) }
    return selectImpl(init)
}

fun <R> Body.table(init: Body.() -> R): R {
    contract { callsInPlace(init, InvocationKind.EXACTLY_ONCE) }
    return tableImpl(init)
}

fun <R> Body.thead(init: Body.() -> R): R {
    contract { callsInPlace(init, InvocationKind.EXACTLY_ONCE) }
    return theadImpl(init)
}

fun <R> Body.tbody(init: Body.() -> R): R {
    contract { callsInPlace(init, InvocationKind.EXACTLY_ONCE) }
    return tbodyImpl(init)
}

fun <R> Body.tr(init: Body.() -> R): R {
    contract { callsInPlace(init, InvocationKind.EXACTLY_ONCE) }
    return trImpl(init)
}

fun <R> Body.td(init: Body.() -> R): R {
    contract { callsInPlace(init, InvocationKind.EXACTLY_ONCE) }
    return tdImpl(init)
}

fun <R> Body.th(init: Body.() -> R): R {
    contract { callsInPlace(init, InvocationKind.EXACTLY_ONCE) }
    return thImpl(init)
}