@file:OptIn(ExperimentalContracts::class)

package de.selfmade4u.tucanpluskmp

import com.fleeksoft.ksoup.nodes.Attribute
import com.fleeksoft.ksoup.nodes.Comment
import com.fleeksoft.ksoup.nodes.DataNode
import com.fleeksoft.ksoup.nodes.Document
import com.fleeksoft.ksoup.nodes.Node
import com.fleeksoft.ksoup.nodes.TextNode
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
}

@HtmlTagMarker
abstract class HtmlTagImpl(val node: Node, val children: MutableList<Node>, val attributes: MutableList<Attribute>) : HtmlTag {
    override fun attribute(key: String, value: String?) {
        val attribute = attributes.removeAt(0)
        if (attribute.key == "class") {
            attribute.setValue(attribute.value.trim())
        }
        if (value == null) {
            check(attribute == Attribute(key, null)) {
                "actual   ${attribute}\nexpected ${
                    Attribute(
                        key,
                        null
                    )
                }"
            }
        } else {
            check(
                attribute == Attribute.createFromEncoded(
                    key,
                    value
                )
            ) {  "Mismatched attribute expected:<${Attribute.createFromEncoded(key, value)}> but was:<${attribute}>" }
        }
    }

    override fun attributeValue(key: String): String {
        val attribute = attributes.removeAt(0)
        if (attribute.key == "class") {
            attribute.setValue(attribute.value.trim())
        }
        check(attribute.key == key) { "actual   ${attribute.key}\nexpected $key" }
        return attribute.value
    }

    override fun extractText(): String {
        check(attributes.isEmpty()) { attributes.removeAt(0) }
        if (this.children.isEmpty()) {
            throw IllegalStateException("${node} actual no children, expected at least one")
        }
        val next = this.children.removeAt(0)
        check(next is TextNode) { "expected text node but got $next" }
        return next.text().trim()
    }

    override fun text(text: String) {
        check(text.trim().isNotEmpty()) { "expected text cannot be empty" }
        check(attributes.isEmpty()) { attributes.removeAt(0) }
        if (this.children.isEmpty()) {
            throw IllegalStateException("${node} actual no children, expected at least one")
        }
        val next = this.children.removeAt(0)
        check(next is TextNode) { next }
        check(next.text().trim() == text) { "Mismatched text expected:<${text}> but was:<${next.text().trim()}>" }
    }

    override fun dataHash(hash: String) {
        check(attributes.isEmpty()) { attributes.removeAt(0) }
        if (this.children.isEmpty()) {
            throw IllegalStateException("${node} actual no children, expected at least one")
        }
        val next = this.children.removeAt(0)
        check(next is DataNode) { next }
        // https://github.com/JetBrains/intellij-community/blob/master/java/java-runtime/src/com/intellij/rt/execution/testFrameworks/AbstractExpectedPatterns.java#L10
        // https://github.com/JetBrains/intellij-community/blob/master/plugins/junit_rt/src/com/intellij/junit4/ExpectedPatterns.java
        // /home/moritz/Documents/intellij-community/plugins/gradle/java/src/execution/test/runner/events/AssertionMessageParser.kt
        /*check(next.getWholeData().hashedWithSha256() == hash) {
            "Mismatched Hash ${next.getWholeData()} expected:<${hash}> but was:<${next.getWholeData().hashedWithSha256()}>"
        }*/
    }

    override fun extractData(): String {
        check(attributes.isEmpty()) { attributes.removeAt(0) }
        if (this.children.isEmpty()) {
            throw IllegalStateException("${node} actual no children, expected at least one")
        }
        val next = this.children.removeAt(0)
        check(next is DataNode) { next }
        return next.getWholeData()
    }
}

interface Root : HtmlTag

class RootImpl(node: Node, nodeList: MutableList<Node>) : Root, HtmlTagImpl(node, nodeList, mutableListOf())

interface Doctype : HtmlTag

class DoctypeImpl(node: Node, nodeList: MutableList<Node>, attributes: MutableList<Attribute>) :
    HtmlTagImpl(node, nodeList, attributes)

class Html(node: Node, nodeList: MutableList<Node>, attributes: MutableList<Attribute>) :
    HtmlTag(node, nodeList, attributes)

class Head(node: Node, nodeList: MutableList<Node>, attributes: MutableList<Attribute>) :
    HtmlTag(node, nodeList, attributes)

class Body(node: Node, nodeList: MutableList<Node>, attributes: MutableList<Attribute>) :
    HtmlTag(node, nodeList, attributes)

class Title(node: Node, nodeList: MutableList<Node>, attributes: MutableList<Attribute>) :
    HtmlTag(node, nodeList, attributes)

class Meta(node: Node, nodeList: MutableList<Node>, attributes: MutableList<Attribute>) :
    HtmlTag(node, nodeList, attributes)

class Link(node: Node, nodeList: MutableList<Node>, attributes: MutableList<Attribute>) :
    HtmlTag(node, nodeList, attributes)

class Script(node: Node, nodeList: MutableList<Node>, attributes: MutableList<Attribute>) :
    HtmlTag(node, nodeList, attributes)

fun shouldIgnore(node: Node): Boolean =
    node is Comment || (node is TextNode && node.text().replace("\\r\\n", "").trim().isEmpty())

fun <T> root(document: Document, init: Root.() -> T): T {
    check(document.nameIs("#root")) { document.normalName() }
    check(document.attributesSize() == 0) { document.attributes() }
    val node = Root(
        document,
        document.childNodes()
            .filterNot(::shouldIgnore)
            .toMutableList()
    )
    return node.init()
}

fun <T> Root.doctype(init: Doctype.() -> T): T = initTag("#doctype", ::Doctype, init)
fun <R> Root.html(init: Html.() -> R): R = initTag("html", ::Html, init)
fun <R> Html.head(init: Head.() -> R): R = initTag("head", ::Head, init)
fun <R> Html.body(init: Body.() -> R): R = initTag("body", ::Body, init)
fun <R> Head.title(init: Title.() -> R): R = initTag("title", ::Title, init)
fun <R> Head.meta(init: Meta.() -> R): R = initTag("meta", ::Meta, init)
fun <R> Head.link(init: Link.() -> R): R = initTag("link", ::Link, init)
fun <R> Head.script(init: Script.() -> R): R = initTag("script", ::Script, init)
fun <R> Head.style(init: Head.() -> R): R = initTag("style", ::Head, init)

fun <R> Body.script(init: Script.() -> R): R = initTag("script", ::Script, init)
fun <R> Body.style(init: Body.() -> R): R = initTag("style", ::Body, init)
fun <R> Body.a(init: Body.() -> R): R {
    contract { callsInPlace(init, InvocationKind.EXACTLY_ONCE) }
    return initTag("a", ::Body, init)
}

fun <R> Body.div(init: Body.() -> R): R = initTag("div", ::Body, init)
fun <R> Body.form(init: Body.() -> R): R = initTag("form", ::Body, init)
fun <R> Body.fieldset(init: Body.() -> R): R = initTag("fieldset", ::Body, init)
fun <R> Body.img(init: Body.() -> R): R = initTag("img", ::Body, init)
fun <R> Body.legend(init: Body.() -> R): R = initTag("legend", ::Body, init)
fun <R> Body.label(init: Body.() -> R): R = initTag("label", ::Body, init)
fun <R> Body.h1(init: Body.() -> R): R = initTag("h1", ::Body, init)
fun <R> Body.p(init: Body.() -> R): R = initTag("p", ::Body, init)
fun <R> Body.ul(init: Body.() -> R): R = initTag("ul", ::Body, init)
fun <R> Body.li(init: Body.() -> R): R = initTag("li", ::Body, init)
fun <R> Body.header(init: Body.() -> R): R = initTag("header", ::Body, init)
fun <R> Body.span(init: Body.() -> R): R = initTag("span", ::Body, init)
fun <R> Body.b(init: Body.() -> R): R = initTag("b", ::Body, init)
fun <R> Body.br(init: Body.() -> R): R = initTag("br", ::Body, init)
fun <R> Body.option(init: Body.() -> R): R {
    contract { callsInPlace(init, InvocationKind.EXACTLY_ONCE) }; return initTag(
        "option",
        ::Body,
        init
    )
}

fun <R> Body.input(init: Body.() -> R): R = initTag("input", ::Body, init)
fun <R> Body.select(init: Body.() -> R): R = initTag("select", ::Body, init)
fun <R> Body.table(init: Body.() -> R): R = initTag("table", ::Body, init)
fun <R> Body.thead(init: Body.() -> R): R = initTag("thead", ::Body, init)
fun <R> Body.tbody(init: Body.() -> R): R = initTag("tbody", ::Body, init)

@OptIn(ExperimentalContracts::class)
fun <R> Body.tr(init: Body.() -> R): R {
    contract {
        callsInPlace(init, InvocationKind.EXACTLY_ONCE)
    }
    return initTag("tr", ::Body, init)
}

@OptIn(ExperimentalContracts::class)
fun <R> Body.td(init: Body.() -> R): R {
    contract {
        callsInPlace(init, InvocationKind.EXACTLY_ONCE)
    }
    return initTag("td", ::Body, init)
}

fun <R> Body.th(init: Body.() -> R): R = initTag("th", ::Body, init)

fun HtmlTag.peek(): Node? {
    return this.children.firstOrNull()
}

fun HtmlTag.peekAttribute(): Attribute? {
    return this.attributes.firstOrNull()
}

@OptIn(ExperimentalContracts::class)
fun <P : HtmlTag, C, R> P.initTag(
    tag: String,
    createTag: (node: Node, iterator: MutableList<Node>, attributes: MutableList<Attribute>) -> C,
    init:  C.() -> R
): R {
    contract {
        callsInPlace(init, InvocationKind.EXACTLY_ONCE)
    }
    if (this.children.isEmpty()) {
        throw IllegalStateException("${node} actual no children, expected at least one")
    }
    val next = this.children.removeAt(0)
    check(next.nameIs(tag)) { "actual   ${next.normalName()} expected $tag" }
    val attributes = next.attributes().toMutableList()
    val childIterator = next.childNodes().filterNot(::shouldIgnore).toMutableList()
    val node = createTag(next, childIterator, attributes)
    val ret = node.init()
    check(attributes.isEmpty()) { "${next.normalName()} unparsed attributes ${attributes.removeAt(0)}" }
    check(childIterator.isEmpty()) { "unparsed children in $tag ${childIterator}" }
    return ret
}