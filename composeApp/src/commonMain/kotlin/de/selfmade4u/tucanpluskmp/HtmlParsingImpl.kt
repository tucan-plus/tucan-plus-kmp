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
                "actual   ${attribute}\nexpected ${Attribute(key, null)}"
            }
        } else {
            check(attribute == Attribute.createFromEncoded(key, value)) {
                "Mismatched attribute expected:<${Attribute.createFromEncoded(key, value)}> but was:<${attribute}>"
            }
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
        check(attributes.isEmpty()) { "Unparsed attributes before text: ${attributes[0]}" }
        if (this.children.isEmpty()) throw IllegalStateException("$node actual no children, expected text")
        val next = this.children.removeAt(0)
        check(next is TextNode) { "expected text node but got $next" }
        return next.text().trim()
    }

    override fun text(text: String) {
        check(text.trim().isNotEmpty()) { "expected text cannot be empty" }
        check(attributes.isEmpty()) { "Unparsed attributes before text: ${attributes[0]}" }
        if (this.children.isEmpty()) throw IllegalStateException("$node actual no children, expected text")
        val next = this.children.removeAt(0)
        check(next is TextNode) { next }
        check(next.text().trim() == text) { "Mismatched text expected:<$text> but was:<${next.text().trim()}>" }
    }

    override fun dataHash(hash: String) {
        check(attributes.isEmpty()) { attributes.removeAt(0) }
        val next = this.children.removeAt(0)
        check(next is DataNode) { next }
    }

    override fun extractData(): String {
        check(attributes.isEmpty()) { attributes.removeAt(0) }
        val next = this.children.removeAt(0)
        check(next is DataNode) { next }
        return next.getWholeData()
    }

    protected fun <T : HtmlTag, S : BaseContentScope, B : TagBuilder<T, S>> initBuilder(
        tag: String,
        createTag: (node: Node, iterator: MutableList<Node>, attributes: MutableList<Attribute>) -> T,
        createBuilder: (impl: GenericTagBuilder<T, S>) -> B
    ): B {
        if (this.children.isEmpty()) throw IllegalStateException("$node actual no children, expected $tag")
        val next = this.children.removeAt(0)
        check(next.nameIs(tag)) { "actual   ${next.normalName()} expected $tag" }

        val attrs = next.attributes().toMutableList()
        val childIter = next.childNodes().filterNot(::shouldIgnore).toMutableList()
        val tagInstance = createTag(next, childIter, attrs)

        @Suppress("UNCHECKED_CAST")
        val genericBuilder = GenericTagBuilder(next, childIter, attrs, tagInstance, tagInstance as S)
        return createBuilder(genericBuilder)
    }

    override fun peek(): de.selfmade4u.tucanpluskmp.Node? = this.children.firstOrNull()?.let { KsoupNode(it) }
    override fun peekAttribute(): de.selfmade4u.tucanpluskmp.Attribute? = this.attributes.firstOrNull()?.let { KsoupAttribute(it) }
}

class GenericTagBuilder<T : HtmlTag, out S : BaseContentScope>(
    val next: Node,
    val childIterator: MutableList<Node>,
    val attributes: MutableList<Attribute>,
    val tagInstance: T,
    val scopeInstance: S
) : TagBuilder<T, S>, TagContentBuilder<T, S> {

    private var attributesExecuted = false

    override fun executeAttributes(init: HtmlAttributeScope.() -> Unit): TagContentBuilder<T, S> {
        tagInstance.init()
        check(attributes.isEmpty()) { "${next.normalName()} unparsed attributes: ${attributes[0]}" }
        attributesExecuted = true
        return this
    }

    override fun <R> executeContent(init: S.() -> R): R {
        if (!attributesExecuted) {
            check(attributes.isEmpty()) { "${next.normalName()} unparsed attributes: ${attributes[0]}" }
        }
        val result = scopeInstance.init()
        check(childIterator.isEmpty()) { "unparsed children in ${next.normalName()}: $childIterator" }
        return result
    }
}

// --- Implementation Classes ---

class RootImpl(node: Node, nodeList: MutableList<Node>) : Root, HtmlTagImpl(node, nodeList, mutableListOf()) {
    override val doctype: DoctypeBuilder get() = initBuilder("#doctype", ::DoctypeImpl, ::DoctypeBuilderImpl)
    override val html: HtmlBuilder get() = initBuilder("html", ::HtmlImpl, ::HtmlBuilderImpl)
}

class DoctypeImpl(node: Node, nodeList: MutableList<Node>, attributes: MutableList<Attribute>) : Doctype, HtmlTagImpl(node, nodeList, attributes)

class HtmlImpl(node: Node, nodeList: MutableList<Node>, attributes: MutableList<Attribute>) : Html, HtmlTagImpl(node, nodeList, attributes) {
    override val head: HeadBuilder get() = initBuilder("head", ::HeadImpl, ::HeadBuilderImpl)
    override val body: BodyBuilder get() = initBuilder("body", ::BodyImpl, ::BodyBuilderImpl)
}

class HeadImpl(node: Node, nodeList: MutableList<Node>, attributes: MutableList<Attribute>) : Head, HtmlTagImpl(node, nodeList, attributes) {
    override val title: TitleBuilder get() = initBuilder("title", ::TitleImpl, ::TitleBuilderImpl)
    override val meta: MetaBuilder get() = initBuilder("meta", ::MetaImpl, ::MetaBuilderImpl)
    override val link: LinkBuilder get() = initBuilder("link", ::LinkImpl, ::LinkBuilderImpl)
    override val style: StyleHeadBuilder get() = initBuilder("style", ::HeadImpl, ::StyleHeadBuilderImpl)
    override val script: ScriptBuilder get() = initBuilder("script", ::ScriptImpl, ::ScriptBuilderImpl)
}

class BodyImpl(node: Node, nodeList: MutableList<Node>, attributes: MutableList<Attribute>) : Body, HtmlTagImpl(node, nodeList, attributes) {
    override val script get() = initBuilder("script", ::ScriptImpl, ::ScriptBuilderImpl)
    override val style get() = initBuilder("style", ::BodyImpl, ::StyleBodyBuilderImpl)
    override val a get() = initBuilder("a", ::BodyImpl, ::ABuilderImpl)
    override val div get() = initBuilder("div", ::BodyImpl, ::DivBuilderImpl)
    override val form get() = initBuilder("form", ::BodyImpl, ::FormBuilderImpl)
    override val fieldset get() = initBuilder("fieldset", ::BodyImpl, ::FieldsetBuilderImpl)
    override val img get() = initBuilder("img", ::BodyImpl, ::ImgBuilderImpl)
    override val legend get() = initBuilder("legend", ::BodyImpl, ::LegendBuilderImpl)
    override val label get() = initBuilder("label", ::BodyImpl, ::LabelBuilderImpl)
    override val h1 get() = initBuilder("h1", ::BodyImpl, ::H1BuilderImpl)
    override val p get() = initBuilder("p", ::BodyImpl, ::PBuilderImpl)
    override val ul get() = initBuilder("ul", ::BodyImpl, ::UlBuilderImpl)
    override val li get() = initBuilder("li", ::BodyImpl, ::LiBuilderImpl)
    override val header get() = initBuilder("header", ::BodyImpl, ::HeaderBuilderImpl)
    override val span get() = initBuilder("span", ::BodyImpl, ::SpanBuilderImpl)
    override val b get() = initBuilder("b", ::BodyImpl, ::BBuilderImpl)
    override val br get() = initBuilder("br", ::BodyImpl, ::BrBuilderImpl)
    override val option get() = initBuilder("option", ::BodyImpl, ::OptionBuilderImpl)
    override val input get() = initBuilder("input", ::BodyImpl, ::InputBuilderImpl)
    override val select get() = initBuilder("select", ::BodyImpl, ::SelectBuilderImpl)
    override val table get() = initBuilder("table", ::BodyImpl, ::TableBuilderImpl)
    override val thead get() = initBuilder("thead", ::BodyImpl, ::TheadBuilderImpl)
    override val tbody get() = initBuilder("tbody", ::BodyImpl, ::TbodyBuilderImpl)
    override val tr get() = initBuilder("tr", ::BodyImpl, ::TrBuilderImpl)
    override val td get() = initBuilder("td", ::BodyImpl, ::TdBuilderImpl)
    override val th get() = initBuilder("th", ::BodyImpl, ::ThBuilderImpl)
}

class TitleImpl(node: Node, nodeList: MutableList<Node>, attributes: MutableList<Attribute>) : Title, HtmlTagImpl(node, nodeList, attributes)
class MetaImpl(node: Node, nodeList: MutableList<Node>, attributes: MutableList<Attribute>) : Meta, HtmlTagImpl(node, nodeList, attributes)
class LinkImpl(node: Node, nodeList: MutableList<Node>, attributes: MutableList<Attribute>) : Link, HtmlTagImpl(node, nodeList, attributes)
private class ScriptImpl(node: Node, nodeList: MutableList<Node>, attributes: MutableList<Attribute>) : Script, HtmlTagImpl(node, nodeList, attributes)

// --- Builder Delegation Proxies ---

class DoctypeBuilderImpl(d: GenericTagBuilder<Doctype, BaseContentScope>) : DoctypeBuilder, TagBuilder<Doctype, BaseContentScope> by d
class HtmlBuilderImpl(d: GenericTagBuilder<Html, HtmlContentScope>) : HtmlBuilder, TagBuilder<Html, HtmlContentScope> by d
class HeadBuilderImpl(d: GenericTagBuilder<Head, HeadContentScope>) : HeadBuilder, TagBuilder<Head, HeadContentScope> by d
class BodyBuilderImpl(d: GenericTagBuilder<Body, BodyContentScope>) : BodyBuilder, TagBuilder<Body, BodyContentScope> by d

class TitleBuilderImpl(d: GenericTagBuilder<Title, BaseContentScope>) : TitleBuilder, TagBuilder<Title, BaseContentScope> by d
class MetaBuilderImpl(d: GenericTagBuilder<Meta, BaseContentScope>) : MetaBuilder, TagBuilder<Meta, BaseContentScope> by d
class LinkBuilderImpl(d: GenericTagBuilder<Link, BaseContentScope>) : LinkBuilder, TagBuilder<Link, BaseContentScope> by d
class ScriptBuilderImpl(d: GenericTagBuilder<Script, BaseContentScope>) : ScriptBuilder, TagBuilder<Script, BaseContentScope> by d
class StyleHeadBuilderImpl(d: GenericTagBuilder<Head, HeadContentScope>) : StyleHeadBuilder, TagBuilder<Head, HeadContentScope> by d
class StyleBodyBuilderImpl(d: GenericTagBuilder<Body, BodyContentScope>) : StyleBodyBuilder, TagBuilder<Body, BodyContentScope> by d
class ABuilderImpl(d: GenericTagBuilder<Body, BodyContentScope>) : ABuilder, TagBuilder<Body, BodyContentScope> by d
class DivBuilderImpl(d: GenericTagBuilder<Body, BodyContentScope>) : DivBuilder, TagBuilder<Body, BodyContentScope> by d
class FormBuilderImpl(d: GenericTagBuilder<Body, BodyContentScope>) : FormBuilder, TagBuilder<Body, BodyContentScope> by d
class FieldsetBuilderImpl(d: GenericTagBuilder<Body, BodyContentScope>) : FieldsetBuilder, TagBuilder<Body, BodyContentScope> by d
class ImgBuilderImpl(d: GenericTagBuilder<Body, BodyContentScope>) : ImgBuilder, TagBuilder<Body, BodyContentScope> by d
class LegendBuilderImpl(d: GenericTagBuilder<Body, BodyContentScope>) : LegendBuilder, TagBuilder<Body, BodyContentScope> by d
class LabelBuilderImpl(d: GenericTagBuilder<Body, BodyContentScope>) : LabelBuilder, TagBuilder<Body, BodyContentScope> by d
class H1BuilderImpl(d: GenericTagBuilder<Body, BodyContentScope>) : H1Builder, TagBuilder<Body, BodyContentScope> by d
class PBuilderImpl(d: GenericTagBuilder<Body, BodyContentScope>) : PBuilder, TagBuilder<Body, BodyContentScope> by d
class UlBuilderImpl(d: GenericTagBuilder<Body, BodyContentScope>) : UlBuilder, TagBuilder<Body, BodyContentScope> by d
class LiBuilderImpl(d: GenericTagBuilder<Body, BodyContentScope>) : LiBuilder, TagBuilder<Body, BodyContentScope> by d
class HeaderBuilderImpl(d: GenericTagBuilder<Body, BodyContentScope>) : HeaderBuilder, TagBuilder<Body, BodyContentScope> by d
class SpanBuilderImpl(d: GenericTagBuilder<Body, BodyContentScope>) : SpanBuilder, TagBuilder<Body, BodyContentScope> by d
class BBuilderImpl(d: GenericTagBuilder<Body, BodyContentScope>) : BBuilder, TagBuilder<Body, BodyContentScope> by d
class BrBuilderImpl(d: GenericTagBuilder<Body, BodyContentScope>) : BrBuilder, TagBuilder<Body, BodyContentScope> by d
class OptionBuilderImpl(d: GenericTagBuilder<Body, BodyContentScope>) : OptionBuilder, TagBuilder<Body, BodyContentScope> by d
class InputBuilderImpl(d: GenericTagBuilder<Body, BodyContentScope>) : InputBuilder, TagBuilder<Body, BodyContentScope> by d
class SelectBuilderImpl(d: GenericTagBuilder<Body, BodyContentScope>) : SelectBuilder, TagBuilder<Body, BodyContentScope> by d
class TableBuilderImpl(d: GenericTagBuilder<Body, BodyContentScope>) : TableBuilder, TagBuilder<Body, BodyContentScope> by d
class TheadBuilderImpl(d: GenericTagBuilder<Body, BodyContentScope>) : TheadBuilder, TagBuilder<Body, BodyContentScope> by d
class TbodyBuilderImpl(d: GenericTagBuilder<Body, BodyContentScope>) : TbodyBuilder, TagBuilder<Body, BodyContentScope> by d
class TrBuilderImpl(d: GenericTagBuilder<Body, BodyContentScope>) : TrBuilder, TagBuilder<Body, BodyContentScope> by d
class TdBuilderImpl(d: GenericTagBuilder<Body, BodyContentScope>) : TdBuilder, TagBuilder<Body, BodyContentScope> by d
class ThBuilderImpl(d: GenericTagBuilder<Body, BodyContentScope>) : ThBuilder, TagBuilder<Body, BodyContentScope> by d

// --- Entry Points ---

fun <T> root(document: Document, init: RootContentScope.() -> T): T {
    check(document.nameIs("#root")) { document.normalName() }
    check(document.attributesSize() == 0) { document.attributes() }
    val node = RootImpl(document, document.childNodes().filterNot(::shouldIgnore).toMutableList())
    return node.init()
}

fun shouldIgnore(node: Node): Boolean =
    node is Comment || (node is TextNode && node.text().replace("\\r\\n", "").trim().isEmpty())