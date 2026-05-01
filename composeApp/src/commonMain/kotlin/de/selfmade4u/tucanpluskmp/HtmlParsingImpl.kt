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
import kotlin.jvm.JvmInline

@JvmInline
value class KsoupNode(private val node: com.fleeksoft.ksoup.nodes.Node) : de.selfmade4u.tucanpluskmp.Node {

}

@JvmInline
value class KsoupAttribute(private val node: com.fleeksoft.ksoup.nodes.Attribute) : de.selfmade4u.tucanpluskmp.Attribute {

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
    fun <C, R> initTag(
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
    override fun peek(): de.selfmade4u.tucanpluskmp.Node? {
        return this.children.firstOrNull()?.let { KsoupNode(it) }
    }
    override fun peekAttribute(): de.selfmade4u.tucanpluskmp.Attribute? {
        return this.attributes.firstOrNull()?.let { KsoupAttribute(it) }
    }
}
class RootImpl(node: Node, nodeList: MutableList<Node>) :
    Root, HtmlTagImpl(node, nodeList, mutableListOf()) {

    override fun <T> doctypeImpl(init: Doctype.() -> T): T =
        initTag("#doctype", ::DoctypeImpl, init)

    override fun <R> htmlImpl(init: Html.() -> R): R =
        initTag("html", ::HtmlImpl, init)
}

class DoctypeImpl(
    node: Node,
    nodeList: MutableList<Node>,
    attributes: MutableList<Attribute>
) : Doctype, HtmlTagImpl(node, nodeList, attributes)

class HtmlImpl(
    node: Node,
    nodeList: MutableList<Node>,
    attributes: MutableList<Attribute>
) : Html, HtmlTagImpl(node, nodeList, attributes) {

    override fun <R> headImpl(init: Head.() -> R): R =
        initTag("head", ::HeadImpl, init)

    override fun <R> bodyImpl(init: Body.() -> R): R =
        initTag("body", ::BodyImpl, init)
}

class HeadImpl(
    node: Node,
    nodeList: MutableList<Node>,
    attributes: MutableList<Attribute>
) : Head, HtmlTagImpl(node, nodeList, attributes) {

    override fun <R> titleImpl(init: Title.() -> R): R =
        initTag("title", ::TitleImpl, init)

    override fun <R> metaImpl(init: Meta.() -> R): R =
        initTag("meta", ::MetaImpl, init)

    override fun <R> linkImpl(init: Link.() -> R): R =
        initTag("link", ::LinkImpl, init)

    override fun <R> styleImpl(init: Head.() -> R): R =
        initTag("style", ::HeadImpl, init)

    override fun <R> scriptImpl(init: Script.() -> R): R =
        initTag("script", ::ScriptImpl, init)
}

class BodyImpl(
    node: Node,
    nodeList: MutableList<Node>,
    attributes: MutableList<Attribute>
) : Body, HtmlTagImpl(node, nodeList, attributes) {

    override fun <R> scriptImpl(init: Script.() -> R): R = initTag("script", ::ScriptImpl, init)
    override fun <R> styleImpl(init: Body.() -> R): R = initTag("style", ::BodyImpl, init)
    override fun <R> aImpl(init: Body.() -> R): R = initTag("a", ::BodyImpl, init)
    override fun <R> divImpl(init: Body.() -> R): R = initTag("div", ::BodyImpl, init)
    override fun <R> formImpl(init: Body.() -> R): R = initTag("form", ::BodyImpl, init)
    override fun <R> fieldsetImpl(init: Body.() -> R): R = initTag("fieldset", ::BodyImpl, init)
    override fun <R> imgImpl(init: Body.() -> R): R = initTag("img", ::BodyImpl, init)
    override fun <R> legendImpl(init: Body.() -> R): R = initTag("legend", ::BodyImpl, init)
    override fun <R> labelImpl(init: Body.() -> R): R = initTag("label", ::BodyImpl, init)
    override fun <R> h1Impl(init: Body.() -> R): R = initTag("h1", ::BodyImpl, init)
    override fun <R> pImpl(init: Body.() -> R): R = initTag("p", ::BodyImpl, init)
    override fun <R> ulImpl(init: Body.() -> R): R = initTag("ul", ::BodyImpl, init)
    override fun <R> liImpl(init: Body.() -> R): R = initTag("li", ::BodyImpl, init)
    override fun <R> headerImpl(init: Body.() -> R): R = initTag("header", ::BodyImpl, init)
    override fun <R> spanImpl(init: Body.() -> R): R = initTag("span", ::BodyImpl, init)
    override fun <R> bImpl(init: Body.() -> R): R = initTag("b", ::BodyImpl, init)
    override fun <R> brImpl(init: Body.() -> R): R = initTag("br", ::BodyImpl, init)
    override fun <R> optionImpl(init: Body.() -> R): R = initTag("option", ::BodyImpl, init)
    override fun <R> inputImpl(init: Body.() -> R): R = initTag("input", ::BodyImpl, init)
    override fun <R> selectImpl(init: Body.() -> R): R = initTag("select", ::BodyImpl, init)
    override fun <R> tableImpl(init: Body.() -> R): R = initTag("table", ::BodyImpl, init)
    override fun <R> theadImpl(init: Body.() -> R): R = initTag("thead", ::BodyImpl, init)
    override fun <R> tbodyImpl(init: Body.() -> R): R = initTag("tbody", ::BodyImpl, init)
    override fun <R> trImpl(init: Body.() -> R): R = initTag("tr", ::BodyImpl, init)
    override fun <R> tdImpl(init: Body.() -> R): R = initTag("td", ::BodyImpl, init)
    override fun <R> thImpl(init: Body.() -> R): R = initTag("th", ::BodyImpl, init)
}

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


private class ScriptImpl(
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

