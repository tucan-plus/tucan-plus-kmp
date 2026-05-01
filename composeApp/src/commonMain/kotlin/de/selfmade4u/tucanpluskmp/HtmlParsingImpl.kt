package de.selfmade4u.tucanpluskmp

import com.fleeksoft.ksoup.nodes.Attribute
import com.fleeksoft.ksoup.nodes.Comment
import com.fleeksoft.ksoup.nodes.DataNode
import com.fleeksoft.ksoup.nodes.Document
import com.fleeksoft.ksoup.nodes.Node
import com.fleeksoft.ksoup.nodes.TextNode
import kotlin.contracts.ExperimentalContracts

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
class RootImpl(node: Node, nodeList: MutableList<Node>) :
    Root, HtmlTagImpl(node, nodeList, mutableListOf())


class DoctypeImpl(
    node: Node,
    nodeList: MutableList<Node>,
    attributes: MutableList<Attribute>
) : Doctype, HtmlTagImpl(node, nodeList, attributes)

class HtmlImpl(
    node: Node,
    nodeList: MutableList<Node>,
    attributes: MutableList<Attribute>
) : Html, HtmlTagImpl(node, nodeList, attributes)


class HeadImpl(
    node: Node,
    nodeList: MutableList<Node>,
    attributes: MutableList<Attribute>
) : Head, HtmlTagImpl(node, nodeList, attributes)

class BodyImpl(
    node: Node,
    nodeList: MutableList<Node>,
    attributes: MutableList<Attribute>
) : Body, HtmlTagImpl(node, nodeList, attributes)

class TitleImpl(
    node: Node,
    nodeList: MutableList<Node>,
    attributes: MutableList<Attribute>
) : Title, HtmlTagImpl(node, nodeList, attributes)

class MetaImpl(
    node: Node,
    nodeList: MutableList<Node>,
    attributes: MutableList<Attribute>
) : Meta, HtmlTagImpl(node, nodeList, attributes)

class LinkImpl(
    node: Node,
    nodeList: MutableList<Node>,
    attributes: MutableList<Attribute>
) : Link, HtmlTagImpl(node, nodeList, attributes)


class ScriptImpl(
    node: Node,
    nodeList: MutableList<Node>,
    attributes: MutableList<Attribute>
) : Script, HtmlTagImpl(node, nodeList, attributes)

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

fun shouldIgnore(node: Node): Boolean =
    node is Comment || (node is TextNode && node.text().replace("\\r\\n", "").trim().isEmpty())

