@file:OptIn(ExperimentalContracts::class)

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

interface Root : HtmlTag
interface Doctype : HtmlTag
interface Html : HtmlTag
interface Head : HtmlTag
interface Body : HtmlTag
interface Title : HtmlTag
interface Meta : HtmlTag
interface Link : HtmlTag
interface Script : HtmlTag

fun <T> Root.doctype(init: Doctype.() -> T): T = initTag("#doctype", ::DoctypeImpl, init)
fun <R> Root.html(init: Html.() -> R): R = initTag("html", ::HtmlImpl, init)
fun <R> Html.head(init: Head.() -> R): R = initTag("head", ::HeadImpl, init)
fun <R> Html.body(init: Body.() -> R): R = initTag("body", ::BodyImpl, init)
fun <R> Head.title(init: Title.() -> R): R = initTag("title", ::TitleImpl, init)
fun <R> Head.meta(init: Meta.() -> R): R = initTag("meta", ::MetaImpl, init)
fun <R> Head.link(init: Link.() -> R): R = initTag("link", ::LinkImpl, init)
fun <R> Head.script(init: Script.() -> R): R = initTag("script", ::ScriptImpl, init)
fun <R> Head.style(init: Head.() -> R): R = initTag("style", ::HeadImpl, init)

fun <R> Body.script(init: Script.() -> R): R = initTag("script", ::ScriptImpl, init)
fun <R> Body.style(init: Body.() -> R): R = initTag("style", ::BodyImpl, init)
fun <R> Body.a(init: Body.() -> R): R {
    contract { callsInPlace(init, InvocationKind.EXACTLY_ONCE) }
    return initTag("a", ::BodyImpl, init)
}

fun <R> Body.div(init: Body.() -> R): R = initTag("div", ::BodyImpl, init)
fun <R> Body.form(init: Body.() -> R): R = initTag("form", ::BodyImpl, init)
fun <R> Body.fieldset(init: Body.() -> R): R = initTag("fieldset", ::BodyImpl, init)
fun <R> Body.img(init: Body.() -> R): R = initTag("img", ::BodyImpl, init)
fun <R> Body.legend(init: Body.() -> R): R = initTag("legend", ::BodyImpl, init)
fun <R> Body.label(init: Body.() -> R): R = initTag("label", ::BodyImpl, init)
fun <R> Body.h1(init: Body.() -> R): R = initTag("h1", ::BodyImpl, init)
fun <R> Body.p(init: Body.() -> R): R = initTag("p", ::BodyImpl, init)
fun <R> Body.ul(init: Body.() -> R): R = initTag("ul", ::BodyImpl, init)
fun <R> Body.li(init: Body.() -> R): R = initTag("li", ::BodyImpl, init)
fun <R> Body.header(init: Body.() -> R): R = initTag("header", ::BodyImpl, init)
fun <R> Body.span(init: Body.() -> R): R = initTag("span", ::BodyImpl, init)
fun <R> Body.b(init: Body.() -> R): R = initTag("b", ::BodyImpl, init)
fun <R> Body.br(init: Body.() -> R): R = initTag("br", ::BodyImpl, init)
fun <R> Body.option(init: Body.() -> R): R {
    contract { callsInPlace(init, InvocationKind.EXACTLY_ONCE) }; return initTag(
        "option",
        ::BodyImpl,
        init
    )
}

fun <R> Body.input(init: Body.() -> R): R = initTag("input", ::BodyImpl, init)
fun <R> Body.select(init: Body.() -> R): R = initTag("select", ::BodyImpl, init)
fun <R> Body.table(init: Body.() -> R): R = initTag("table", ::BodyImpl, init)
fun <R> Body.thead(init: Body.() -> R): R = initTag("thead", ::BodyImpl, init)
fun <R> Body.tbody(init: Body.() -> R): R = initTag("tbody", ::BodyImpl, init)

@OptIn(ExperimentalContracts::class)
fun <R> Body.tr(init: Body.() -> R): R {
    contract {
        callsInPlace(init, InvocationKind.EXACTLY_ONCE)
    }
    return initTag("tr", ::BodyImpl, init)
}

@OptIn(ExperimentalContracts::class)
fun <R> Body.td(init: Body.() -> R): R {
    contract {
        callsInPlace(init, InvocationKind.EXACTLY_ONCE)
    }
    return initTag("td", ::BodyImpl, init)
}

fun <R> Body.th(init: Body.() -> R): R = initTag("th", ::BodyImpl, init)
