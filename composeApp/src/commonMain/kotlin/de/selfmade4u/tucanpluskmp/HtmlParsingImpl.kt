@file:OptIn(ExperimentalContracts::class)
@file:Suppress("LEAKED_IN_PLACE_LAMBDA", "WRONG_INVOCATION_KIND")

package de.selfmade4u.tucanpluskmp

import com.fleeksoft.ksoup.nodes.Attribute
import com.fleeksoft.ksoup.nodes.Comment
import com.fleeksoft.ksoup.nodes.DataNode
import com.fleeksoft.ksoup.nodes.Document
import com.fleeksoft.ksoup.nodes.Node
import com.fleeksoft.ksoup.nodes.TextNode
import kotlinx.coroutines.flow.first
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract
import kotlin.jvm.JvmInline

@JvmInline
value class KsoupNode(private val node: com.fleeksoft.ksoup.nodes.Node) : de.selfmade4u.tucanpluskmp.Node {
    override fun attr(name: String): String = node.attr(name)
    override fun normalName(): String = node.normalName()
    override fun firstChild(): de.selfmade4u.tucanpluskmp.Node = node.childNodes().filterNot(::shouldIgnore).first().let { KsoupNode(it) }
}

@JvmInline
value class KsoupAttribute(private val node: com.fleeksoft.ksoup.nodes.Attribute) : de.selfmade4u.tucanpluskmp.Attribute {
    override val key: String
        get() = node.key
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

    protected fun <T : HtmlTag, B : TagBuilder<T>> initBuilder(
        tag: String,
        createTag: (node: Node, iterator: MutableList<Node>, attributes: MutableList<Attribute>) -> T,
        createBuilder: (impl: GenericTagBuilder<T>) -> B
    ): B {
        if (this.children.isEmpty()) {
            throw IllegalStateException("${node} actual no children, expected at least one")
        }
        val next = this.children.removeAt(0)
        check(next.nameIs(tag)) { "actual   ${next.normalName()} expected $tag" }
        val attributes = next.attributes().toMutableList()
        val childIterator = next.childNodes().filterNot(::shouldIgnore).toMutableList()

        val tagInstance = createTag(next, childIterator, attributes)
        val genericBuilder = GenericTagBuilder(next, childIterator, attributes, tagInstance)

        return createBuilder(genericBuilder)
    }

    override fun peek(): de.selfmade4u.tucanpluskmp.Node? {
        return this.children.firstOrNull()?.let { KsoupNode(it) }
    }

    override fun peekAttribute(): de.selfmade4u.tucanpluskmp.Attribute? {
        return this.attributes.firstOrNull()?.let { KsoupAttribute(it) }
    }
}

class GenericTagBuilder<T : HtmlTag>(
    val next: Node,
    val childIterator: MutableList<Node>,
    val attributes: MutableList<Attribute>,
    val tagInstance: T
) : TagBuilder<T> {

    private var attributesExecuted = false

    override fun executeAttributes(init: T.() -> Unit): TagContentBuilder<T> {
        tagInstance.init()
        check(attributes.isEmpty()) { "${next.normalName()} unparsed attributes ${if (attributes.isNotEmpty()) attributes[0] else ""}" }
        attributesExecuted = true
        return this
    }

    override fun executeContent(init: T.() -> Unit) {
        if (!attributesExecuted) {
            check(attributes.isEmpty()) { "${next.normalName()} unparsed attributes ${if (attributes.isNotEmpty()) attributes[0] else ""}" }
        }
        tagInstance.init()
        check(childIterator.isEmpty()) { "unparsed children in ${next.normalName()} $childIterator" }
    }
}

class RootImpl(node: Node, nodeList: MutableList<Node>) : Root, HtmlTagImpl(node, nodeList, mutableListOf()) {
    override fun doctypeImpl(): DoctypeBuilder = initBuilder("#doctype", ::DoctypeImpl, ::DoctypeBuilderImpl)
    override fun htmlImpl(): HtmlBuilder = initBuilder("html", ::HtmlImpl, ::HtmlBuilderImpl)
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
    override fun headImpl(): HeadBuilder = initBuilder("head", ::HeadImpl, ::HeadBuilderImpl)
    override fun bodyImpl(): BodyBuilder = initBuilder("body", ::BodyImpl, ::BodyBuilderImpl)
}

class HeadImpl(
    node: Node,
    nodeList: MutableList<Node>,
    attributes: MutableList<Attribute>
) : Head, HtmlTagImpl(node, nodeList, attributes) {
    override fun titleImpl(): TitleBuilder = initBuilder("title", ::TitleImpl, ::TitleBuilderImpl)
    override fun metaImpl(): MetaBuilder = initBuilder("meta", ::MetaImpl, ::MetaBuilderImpl)
    override fun linkImpl(): LinkBuilder = initBuilder("link", ::LinkImpl, ::LinkBuilderImpl)
    override fun styleImpl(): StyleHeadBuilder = initBuilder("style", ::HeadImpl, ::StyleHeadBuilderImpl)
    override fun scriptImpl(): ScriptBuilder = initBuilder("script", ::ScriptImpl, ::ScriptBuilderImpl)
}

class BodyImpl(
    node: Node,
    nodeList: MutableList<Node>,
    attributes: MutableList<Attribute>
) : Body, HtmlTagImpl(node, nodeList, attributes) {
    override fun scriptImpl(): ScriptBuilder = initBuilder("script", ::ScriptImpl, ::ScriptBuilderImpl)
    override fun styleImpl(): StyleBodyBuilder = initBuilder("style", ::BodyImpl, ::StyleBodyBuilderImpl)
    override fun aImpl(): ABuilder = initBuilder("a", ::BodyImpl, ::ABuilderImpl)
    override fun divImpl(): DivBuilder = initBuilder("div", ::BodyImpl, ::DivBuilderImpl)
    override fun formImpl(): FormBuilder = initBuilder("form", ::BodyImpl, ::FormBuilderImpl)
    override fun fieldsetImpl(): FieldsetBuilder = initBuilder("fieldset", ::BodyImpl, ::FieldsetBuilderImpl)
    override fun imgImpl(): ImgBuilder = initBuilder("img", ::BodyImpl, ::ImgBuilderImpl)
    override fun legendImpl(): LegendBuilder = initBuilder("legend", ::BodyImpl, ::LegendBuilderImpl)
    override fun labelImpl(): LabelBuilder = initBuilder("label", ::BodyImpl, ::LabelBuilderImpl)
    override fun h1Impl(): H1Builder = initBuilder("h1", ::BodyImpl, ::H1BuilderImpl)
    override fun pImpl(): PBuilder = initBuilder("p", ::BodyImpl, ::PBuilderImpl)
    override fun ulImpl(): UlBuilder = initBuilder("ul", ::BodyImpl, ::UlBuilderImpl)
    override fun liImpl(): LiBuilder = initBuilder("li", ::BodyImpl, ::LiBuilderImpl)
    override fun headerImpl(): HeaderBuilder = initBuilder("header", ::BodyImpl, ::HeaderBuilderImpl)
    override fun spanImpl(): SpanBuilder = initBuilder("span", ::BodyImpl, ::SpanBuilderImpl)
    override fun bImpl(): BBuilder = initBuilder("b", ::BodyImpl, ::BBuilderImpl)
    override fun brImpl(): BrBuilder = initBuilder("br", ::BodyImpl, ::BrBuilderImpl)
    override fun optionImpl(): OptionBuilder = initBuilder("option", ::BodyImpl, ::OptionBuilderImpl)
    override fun inputImpl(): InputBuilder = initBuilder("input", ::BodyImpl, ::InputBuilderImpl)
    override fun selectImpl(): SelectBuilder = initBuilder("select", ::BodyImpl, ::SelectBuilderImpl)
    override fun tableImpl(): TableBuilder = initBuilder("table", ::BodyImpl, ::TableBuilderImpl)
    override fun theadImpl(): TheadBuilder = initBuilder("thead", ::BodyImpl, ::TheadBuilderImpl)
    override fun tbodyImpl(): TbodyBuilder = initBuilder("tbody", ::BodyImpl, ::TbodyBuilderImpl)
    override fun trImpl(): TrBuilder = initBuilder("tr", ::BodyImpl, ::TrBuilderImpl)
    override fun tdImpl(): TdBuilder = initBuilder("td", ::BodyImpl, ::TdBuilderImpl)
    override fun thImpl(): ThBuilder = initBuilder("th", ::BodyImpl, ::ThBuilderImpl)
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

// --- Builder Delegation Proxies ---

class DoctypeBuilderImpl(d: GenericTagBuilder<Doctype>) : DoctypeBuilder, TagBuilder<Doctype> by d
class HtmlBuilderImpl(d: GenericTagBuilder<Html>) : HtmlBuilder, TagBuilder<Html> by d
class HeadBuilderImpl(d: GenericTagBuilder<Head>) : HeadBuilder, TagBuilder<Head> by d
class BodyBuilderImpl(d: GenericTagBuilder<Body>) : BodyBuilder, TagBuilder<Body> by d
class TitleBuilderImpl(d: GenericTagBuilder<Title>) : TitleBuilder, TagBuilder<Title> by d
class MetaBuilderImpl(d: GenericTagBuilder<Meta>) : MetaBuilder, TagBuilder<Meta> by d
class LinkBuilderImpl(d: GenericTagBuilder<Link>) : LinkBuilder, TagBuilder<Link> by d
class ScriptBuilderImpl(d: GenericTagBuilder<Script>) : ScriptBuilder, TagBuilder<Script> by d
class StyleHeadBuilderImpl(d: GenericTagBuilder<Head>) : StyleHeadBuilder, TagBuilder<Head> by d
class StyleBodyBuilderImpl(d: GenericTagBuilder<Body>) : StyleBodyBuilder, TagBuilder<Body> by d
class ABuilderImpl(d: GenericTagBuilder<Body>) : ABuilder, TagBuilder<Body> by d
class DivBuilderImpl(d: GenericTagBuilder<Body>) : DivBuilder, TagBuilder<Body> by d
class FormBuilderImpl(d: GenericTagBuilder<Body>) : FormBuilder, TagBuilder<Body> by d
class FieldsetBuilderImpl(d: GenericTagBuilder<Body>) : FieldsetBuilder, TagBuilder<Body> by d
class ImgBuilderImpl(d: GenericTagBuilder<Body>) : ImgBuilder, TagBuilder<Body> by d
class LegendBuilderImpl(d: GenericTagBuilder<Body>) : LegendBuilder, TagBuilder<Body> by d
class LabelBuilderImpl(d: GenericTagBuilder<Body>) : LabelBuilder, TagBuilder<Body> by d
class H1BuilderImpl(d: GenericTagBuilder<Body>) : H1Builder, TagBuilder<Body> by d
class PBuilderImpl(d: GenericTagBuilder<Body>) : PBuilder, TagBuilder<Body> by d
class UlBuilderImpl(d: GenericTagBuilder<Body>) : UlBuilder, TagBuilder<Body> by d
class LiBuilderImpl(d: GenericTagBuilder<Body>) : LiBuilder, TagBuilder<Body> by d
class HeaderBuilderImpl(d: GenericTagBuilder<Body>) : HeaderBuilder, TagBuilder<Body> by d
class SpanBuilderImpl(d: GenericTagBuilder<Body>) : SpanBuilder, TagBuilder<Body> by d
class BBuilderImpl(d: GenericTagBuilder<Body>) : BBuilder, TagBuilder<Body> by d
class BrBuilderImpl(d: GenericTagBuilder<Body>) : BrBuilder, TagBuilder<Body> by d
class OptionBuilderImpl(d: GenericTagBuilder<Body>) : OptionBuilder, TagBuilder<Body> by d
class InputBuilderImpl(d: GenericTagBuilder<Body>) : InputBuilder, TagBuilder<Body> by d
class SelectBuilderImpl(d: GenericTagBuilder<Body>) : SelectBuilder, TagBuilder<Body> by d
class TableBuilderImpl(d: GenericTagBuilder<Body>) : TableBuilder, TagBuilder<Body> by d
class TheadBuilderImpl(d: GenericTagBuilder<Body>) : TheadBuilder, TagBuilder<Body> by d
class TbodyBuilderImpl(d: GenericTagBuilder<Body>) : TbodyBuilder, TagBuilder<Body> by d
class TrBuilderImpl(d: GenericTagBuilder<Body>) : TrBuilder, TagBuilder<Body> by d
class TdBuilderImpl(d: GenericTagBuilder<Body>) : TdBuilder, TagBuilder<Body> by d
class ThBuilderImpl(d: GenericTagBuilder<Body>) : ThBuilder, TagBuilder<Body> by d

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