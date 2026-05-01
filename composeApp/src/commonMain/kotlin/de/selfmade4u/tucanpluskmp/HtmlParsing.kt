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
interface Root : HtmlTag {
    fun <T> doctype(init: Doctype.() -> T): T
    fun <R> html(init: Html.() -> R): R
}

interface Doctype : HtmlTag

interface Html : HtmlTag {
    fun <R> head(init: Head.() -> R): R
    fun <R> body(init: Body.() -> R): R
}

interface Head : HtmlTag {
    fun <R> title(init: Title.() -> R): R
    fun <R> meta(init: Meta.() -> R): R
    fun <R> link(init: Link.() -> R): R
    fun <R> style(init: Head.() -> R): R
    fun <R> script(init: Script.() -> R): R
}

interface Body : HtmlTag {
    fun <R> script(init: Script.() -> R): R
    fun <R> style(init: Body.() -> R): R
    fun <R> a(init: Body.() -> R): R
    fun <R> div(init: Body.() -> R): R
    fun <R> form(init: Body.() -> R): R
    fun <R> fieldset(init: Body.() -> R): R
    fun <R> img(init: Body.() -> R): R
    fun <R> legend(init: Body.() -> R): R
    fun <R> label(init: Body.() -> R): R
    fun <R> h1(init: Body.() -> R): R
    fun <R> p(init: Body.() -> R): R
    fun <R> ul(init: Body.() -> R): R
    fun <R> li(init: Body.() -> R): R
    fun <R> header(init: Body.() -> R): R
    fun <R> span(init: Body.() -> R): R
    fun <R> b(init: Body.() -> R): R
    fun <R> br(init: Body.() -> R): R
    fun <R> option(init: Body.() -> R): R
    fun <R> input(init: Body.() -> R): R
    fun <R> select(init: Body.() -> R): R
    fun <R> table(init: Body.() -> R): R
    fun <R> thead(init: Body.() -> R): R
    fun <R> tbody(init: Body.() -> R): R
    fun <R> tr(init: Body.() -> R): R
    fun <R> td(init: Body.() -> R): R
    fun <R> th(init: Body.() -> R): R
}

interface Title : HtmlTag
interface Meta : HtmlTag
interface Link : HtmlTag
interface Script : HtmlTag
