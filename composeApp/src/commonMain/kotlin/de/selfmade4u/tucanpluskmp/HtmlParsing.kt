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
    fun <C, R> initTag(
        tag: String,
        createTag: (node: Node, iterator: MutableList<Node>, attributes: MutableList<Attribute>) -> C,
        init:  C.() -> R
    ): R
    // shit here we expose ksoup again
    fun peek(): Node?
    fun peekAttribute(): Attribute?
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

    @OptIn(ExperimentalContracts::class)
    override fun <C, R> initTag(
        tag: String,
        createTag: (node: Node, iterator: MutableList<Node>, attributes: MutableList<Attribute>) -> C,
        init:  C.() -> R
    ): R {
        /*contract {
            callsInPlace(init, InvocationKind.EXACTLY_ONCE)
        }*/
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
    override fun peek(): Node? {
        return this.children.firstOrNull()
    }
    override fun peekAttribute(): Attribute? {
        return this.attributes.firstOrNull()
    }
}

interface Root : HtmlTag

class RootImpl(node: Node, nodeList: MutableList<Node>) :
    Root, HtmlTagImpl(node, nodeList, mutableListOf())


interface Doctype : HtmlTag

class DoctypeImpl(
    node: Node,
    nodeList: MutableList<Node>,
    attributes: MutableList<Attribute>
) : Doctype, HtmlTagImpl(node, nodeList, attributes)


interface Html : HtmlTag

class HtmlImpl(
    node: Node,
    nodeList: MutableList<Node>,
    attributes: MutableList<Attribute>
) : Html, HtmlTagImpl(node, nodeList, attributes)


interface Head : HtmlTag

class HeadImpl(
    node: Node,
    nodeList: MutableList<Node>,
    attributes: MutableList<Attribute>
) : Head, HtmlTagImpl(node, nodeList, attributes)


interface Body : HtmlTag

class BodyImpl(
    node: Node,
    nodeList: MutableList<Node>,
    attributes: MutableList<Attribute>
) : Body, HtmlTagImpl(node, nodeList, attributes)


interface Title : HtmlTag

class TitleImpl(
    node: Node,
    nodeList: MutableList<Node>,
    attributes: MutableList<Attribute>
) : Title, HtmlTagImpl(node, nodeList, attributes)


interface Meta : HtmlTag

class MetaImpl(
    node: Node,
    nodeList: MutableList<Node>,
    attributes: MutableList<Attribute>
) : Meta, HtmlTagImpl(node, nodeList, attributes)


interface Link : HtmlTag

class LinkImpl(
    node: Node,
    nodeList: MutableList<Node>,
    attributes: MutableList<Attribute>
) : Link, HtmlTagImpl(node, nodeList, attributes)


interface Script : HtmlTag

class ScriptImpl(
    node: Node,
    nodeList: MutableList<Node>,
    attributes: MutableList<Attribute>
) : Script, HtmlTagImpl(node, nodeList, attributes)

fun shouldIgnore(node: Node): Boolean =
    node is Comment || (node is TextNode && node.text().replace("\\r\\n", "").trim().isEmpty())

fun <T> root(document: Document, init: Root.() -> T): T {
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
