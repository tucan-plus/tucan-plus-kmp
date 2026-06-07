@file:OptIn(ExperimentalContracts::class)
@file:Suppress("LEAKED_IN_PLACE_LAMBDA", "WRONG_INVOCATION_KIND")

package de.selfmade4u.tucanpluskmp

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

interface Node {
    fun attr(name: String): String
    fun normalName(): String
    fun firstChild(): Node
}

interface Attribute {
    val key: String
}
@HtmlTagMarker
interface HtmlAttributeScope {
    fun attribute(key: String, value: String?)
    fun attributeValue(key: String): String
    fun peekAttribute(): Attribute?
}

@HtmlTagMarker
interface BaseContentScope {
    fun extractText(): String
    fun text(text: String)
    fun dataHash(hash: String)
    fun extractData(): String
    fun peek(): Node?
}

interface RootContentScope : BaseContentScope {
    val doctype: DoctypeBuilder
    val html: HtmlBuilder
}

interface HtmlContentScope : BaseContentScope {
    val head: HeadBuilder
    val body: BodyBuilder
}

interface HeadContentScope : BaseContentScope {
    val title: TitleBuilder
    val meta: MetaBuilder
    val link: LinkBuilder
    val style: StyleHeadBuilder
    val script: ScriptBuilder
}

interface BodyContentScope : BaseContentScope {
    val script: ScriptBuilder
    val style: StyleBodyBuilder
    val a: ABuilder
    val div: DivBuilder
    val form: FormBuilder
    val fieldset: FieldsetBuilder
    val img: ImgBuilder
    val legend: LegendBuilder
    val label: LabelBuilder
    val h1: H1Builder
    val h2: H2Builder
    val p: PBuilder
    val strong: StrongBuilder
    val ul: UlBuilder
    val li: LiBuilder
    val header: HeaderBuilder
    val span: SpanBuilder
    val b: BBuilder
    val br: BrBuilder
    val option: OptionBuilder
    val input: InputBuilder
    val select: SelectBuilder
    val table: TableBuilder
    val thead: TheadBuilder
    val tbody: TbodyBuilder
    val tr: TrBuilder
    val td: TdBuilder
    val th: ThBuilder
}

// --- Tag Interfaces ---

interface HtmlTag : HtmlAttributeScope, BaseContentScope

interface Root : HtmlTag, RootContentScope
interface Doctype : HtmlTag
interface Html : HtmlTag, HtmlContentScope
interface Head : HtmlTag, HeadContentScope
interface Body : HtmlTag, BodyContentScope

interface Title : HtmlTag
interface Meta : HtmlTag
interface Link : HtmlTag
interface Script : HtmlTag

// --- Builder Infrastructure ---

interface TagBuilder<T : HtmlTag, out S : BaseContentScope> : TagContentBuilder<T, S> {
    fun executeAttributes(init: HtmlAttributeScope.() -> Unit): TagContentBuilder<T, S>
}

interface TagContentBuilder<T : HtmlTag, out S : BaseContentScope> {
    fun <R> executeContent(init: S.() -> R): R
}

@OptIn(ExperimentalContracts::class)
fun <T : HtmlTag, S : BaseContentScope> TagBuilder<T, S>.attributes(init: HtmlAttributeScope.() -> Unit): TagContentBuilder<T, S> {
    contract { callsInPlace(init, InvocationKind.EXACTLY_ONCE) }
    return executeAttributes(init)
}

@OptIn(ExperimentalContracts::class)
fun <T : HtmlTag, S : BaseContentScope, R> TagContentBuilder<T, S>.content(init: S.() -> R): R {
    contract { callsInPlace(init, InvocationKind.EXACTLY_ONCE) }
    return executeContent(init)
}

// --- Concrete Builder Definitions ---

interface DoctypeBuilder : TagBuilder<Doctype, BaseContentScope>
interface HtmlBuilder : TagBuilder<Html, HtmlContentScope>
interface HeadBuilder : TagBuilder<Head, HeadContentScope>
interface BodyBuilder : TagBuilder<Body, BodyContentScope>

interface TitleBuilder : TagBuilder<Title, BaseContentScope>
interface MetaBuilder : TagBuilder<Meta, BaseContentScope>
interface LinkBuilder : TagBuilder<Link, BaseContentScope>
interface StyleHeadBuilder : TagBuilder<Head, HeadContentScope>
interface ScriptBuilder : TagBuilder<Script, BaseContentScope>

interface StyleBodyBuilder : TagBuilder<Body, BodyContentScope>
interface ABuilder : TagBuilder<Body, BodyContentScope>
interface DivBuilder : TagBuilder<Body, BodyContentScope>
interface FormBuilder : TagBuilder<Body, BodyContentScope>
interface FieldsetBuilder : TagBuilder<Body, BodyContentScope>
interface ImgBuilder : TagBuilder<Body, BodyContentScope>
interface LegendBuilder : TagBuilder<Body, BodyContentScope>
interface LabelBuilder : TagBuilder<Body, BodyContentScope>
interface H1Builder : TagBuilder<Body, BodyContentScope>
interface H2Builder : TagBuilder<Body, BodyContentScope>
interface PBuilder : TagBuilder<Body, BodyContentScope>
interface StrongBuilder : TagBuilder<Body, BodyContentScope>
interface UlBuilder : TagBuilder<Body, BodyContentScope>
interface LiBuilder : TagBuilder<Body, BodyContentScope>
interface HeaderBuilder : TagBuilder<Body, BodyContentScope>
interface SpanBuilder : TagBuilder<Body, BodyContentScope>
interface BBuilder : TagBuilder<Body, BodyContentScope>
interface BrBuilder : TagBuilder<Body, BodyContentScope>
interface OptionBuilder : TagBuilder<Body, BodyContentScope>
interface InputBuilder : TagBuilder<Body, BodyContentScope>
interface SelectBuilder : TagBuilder<Body, BodyContentScope>
interface TableBuilder : TagBuilder<Body, BodyContentScope>
interface TheadBuilder : TagBuilder<Body, BodyContentScope>
interface TbodyBuilder : TagBuilder<Body, BodyContentScope>
interface TrBuilder : TagBuilder<Body, BodyContentScope>
interface TdBuilder : TagBuilder<Body, BodyContentScope>
interface ThBuilder : TagBuilder<Body, BodyContentScope>