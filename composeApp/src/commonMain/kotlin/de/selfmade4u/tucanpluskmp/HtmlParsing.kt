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

interface HtmlTag {
    fun attribute(key: String, value: String?)
    fun attributeValue(key: String): String
    fun extractText(): String
    fun text(text: String)
    fun dataHash(hash: String)
    fun extractData(): String
    // shit here we expose ksoup again
    fun peek(): Node?
    fun peekAttribute(): Attribute?
}

interface Root : HtmlTag {
    fun doctypeImpl(): DoctypeBuilder
    fun htmlImpl(): HtmlBuilder
}

interface Doctype : HtmlTag

interface Html : HtmlTag {
    fun headImpl(): HeadBuilder
    fun bodyImpl(): BodyBuilder
}

interface Head : HtmlTag {
    fun titleImpl(): TitleBuilder
    fun metaImpl(): MetaBuilder
    fun linkImpl(): LinkBuilder
    fun styleImpl(): StyleHeadBuilder
    fun scriptImpl(): ScriptBuilder
}

sealed interface Body : HtmlTag {
    fun scriptImpl(): ScriptBuilder
    fun styleImpl(): StyleBodyBuilder
    fun aImpl(): ABuilder
    fun divImpl(): DivBuilder
    fun formImpl(): FormBuilder
    fun fieldsetImpl(): FieldsetBuilder
    fun imgImpl(): ImgBuilder
    fun legendImpl(): LegendBuilder
    fun labelImpl(): LabelBuilder
    fun h1Impl(): H1Builder
    fun pImpl(): PBuilder
    fun ulImpl(): UlBuilder
    fun liImpl(): LiBuilder
    fun headerImpl(): HeaderBuilder
    fun spanImpl(): SpanBuilder
    fun bImpl(): BBuilder
    fun brImpl(): BrBuilder
    fun optionImpl(): OptionBuilder
    fun inputImpl(): InputBuilder
    fun selectImpl(): SelectBuilder
    fun tableImpl(): TableBuilder
    fun theadImpl(): TheadBuilder
    fun tbodyImpl(): TbodyBuilder
    fun trImpl(): TrBuilder
    fun tdImpl(): TdBuilder
    fun thImpl(): ThBuilder
}

interface Title : HtmlTag
interface Meta : HtmlTag
interface Link : HtmlTag
interface Script : HtmlTag

// --- Entry Properties ---

val Root.doctype: DoctypeBuilder get() = doctypeImpl()
val Root.html: HtmlBuilder get() = htmlImpl()

val Html.head: HeadBuilder get() = headImpl()
val Html.body: BodyBuilder get() = bodyImpl()

val Head.title: TitleBuilder get() = titleImpl()
val Head.meta: MetaBuilder get() = metaImpl()
val Head.link: LinkBuilder get() = linkImpl()
val Head.style: StyleHeadBuilder get() = styleImpl()
val Head.script: ScriptBuilder get() = scriptImpl()

val Body.script: ScriptBuilder get() = scriptImpl()
val Body.style: StyleBodyBuilder get() = styleImpl()
val Body.a: ABuilder get() = aImpl()
val Body.div: DivBuilder get() = divImpl()
val Body.form: FormBuilder get() = formImpl()
val Body.fieldset: FieldsetBuilder get() = fieldsetImpl()
val Body.img: ImgBuilder get() = imgImpl()
val Body.legend: LegendBuilder get() = legendImpl()
val Body.label: LabelBuilder get() = labelImpl()
val Body.h1: H1Builder get() = h1Impl()
val Body.p: PBuilder get() = pImpl()
val Body.ul: UlBuilder get() = ulImpl()
val Body.li: LiBuilder get() = liImpl()
val Body.header: HeaderBuilder get() = headerImpl()
val Body.span: SpanBuilder get() = spanImpl()
val Body.b: BBuilder get() = bImpl()
val Body.br: BrBuilder get() = brImpl()
val Body.option: OptionBuilder get() = optionImpl()
val Body.input: InputBuilder get() = inputImpl()
val Body.select: SelectBuilder get() = selectImpl()
val Body.table: TableBuilder get() = tableImpl()
val Body.thead: TheadBuilder get() = theadImpl()
val Body.tbody: TbodyBuilder get() = tbodyImpl()
val Body.tr: TrBuilder get() = trImpl()
val Body.td: TdBuilder get() = tdImpl()
val Body.th: ThBuilder get() = thImpl()

// --- Base Builders ---

interface TagBuilder<T : HtmlTag> : TagContentBuilder<T> {
    fun executeAttributes(init: T.() -> Unit): TagContentBuilder<T>
}

interface TagContentBuilder<T : HtmlTag> {
    fun executeContent(init: T.() -> Unit)
}

// Global Extension Functions for all builders
fun <T : HtmlTag> TagBuilder<T>.attributes(init: T.() -> Unit): TagContentBuilder<T> {
    contract { callsInPlace(init, InvocationKind.EXACTLY_ONCE) }
    return executeAttributes(init)
}

fun <T : HtmlTag> TagContentBuilder<T>.content(init: T.() -> Unit) {
    contract { callsInPlace(init, InvocationKind.EXACTLY_ONCE) }
    executeContent(init)
}

// --- Specific Builders ---

interface DoctypeBuilder : TagBuilder<Doctype>
interface DoctypeContentBuilder : TagContentBuilder<Doctype>

interface HtmlBuilder : TagBuilder<Html>
interface HtmlContentBuilder : TagContentBuilder<Html>

interface HeadBuilder : TagBuilder<Head>
interface HeadContentBuilder : TagContentBuilder<Head>

interface BodyBuilder : TagBuilder<Body>
interface BodyContentBuilder : TagContentBuilder<Body>

interface TitleBuilder : TagBuilder<Title>
interface TitleContentBuilder : TagContentBuilder<Title>

interface MetaBuilder : TagBuilder<Meta>
interface MetaContentBuilder : TagContentBuilder<Meta>

interface LinkBuilder : TagBuilder<Link>
interface LinkContentBuilder : TagContentBuilder<Link>

interface StyleHeadBuilder : TagBuilder<Head>
interface StyleHeadContentBuilder : TagContentBuilder<Head>

interface ScriptBuilder : TagBuilder<Script>
interface ScriptContentBuilder : TagContentBuilder<Script>

interface StyleBodyBuilder : TagBuilder<Body>
interface StyleBodyContentBuilder : TagContentBuilder<Body>

interface ABuilder : TagBuilder<Body>
interface AContentBuilder : TagContentBuilder<Body>

interface DivBuilder : TagBuilder<Body>
interface DivContentBuilder : TagContentBuilder<Body>

interface FormBuilder : TagBuilder<Body>
interface FormContentBuilder : TagContentBuilder<Body>

interface FieldsetBuilder : TagBuilder<Body>
interface FieldsetContentBuilder : TagContentBuilder<Body>

interface ImgBuilder : TagBuilder<Body>
interface ImgContentBuilder : TagContentBuilder<Body>

interface LegendBuilder : TagBuilder<Body>
interface LegendContentBuilder : TagContentBuilder<Body>

interface LabelBuilder : TagBuilder<Body>
interface LabelContentBuilder : TagContentBuilder<Body>

interface H1Builder : TagBuilder<Body>
interface H1ContentBuilder : TagContentBuilder<Body>

interface PBuilder : TagBuilder<Body>
interface PContentBuilder : TagContentBuilder<Body>

interface UlBuilder : TagBuilder<Body>
interface UlContentBuilder : TagContentBuilder<Body>

interface LiBuilder : TagBuilder<Body>
interface LiContentBuilder : TagContentBuilder<Body>

interface HeaderBuilder : TagBuilder<Body>
interface HeaderContentBuilder : TagContentBuilder<Body>

interface SpanBuilder : TagBuilder<Body>
interface SpanContentBuilder : TagContentBuilder<Body>

interface BBuilder : TagBuilder<Body>
interface BContentBuilder : TagContentBuilder<Body>

interface BrBuilder : TagBuilder<Body>
interface BrContentBuilder : TagContentBuilder<Body>

interface OptionBuilder : TagBuilder<Body>
interface OptionContentBuilder : TagContentBuilder<Body>

interface InputBuilder : TagBuilder<Body>
interface InputContentBuilder : TagContentBuilder<Body>

interface SelectBuilder : TagBuilder<Body>
interface SelectContentBuilder : TagContentBuilder<Body>

interface TableBuilder : TagBuilder<Body>
interface TableContentBuilder : TagContentBuilder<Body>

interface TheadBuilder : TagBuilder<Body>
interface TheadContentBuilder : TagContentBuilder<Body>

interface TbodyBuilder : TagBuilder<Body>
interface TbodyContentBuilder : TagContentBuilder<Body>

interface TrBuilder : TagBuilder<Body>
interface TrContentBuilder : TagContentBuilder<Body>

interface TdBuilder : TagBuilder<Body>
interface TdContentBuilder : TagContentBuilder<Body>

interface ThBuilder : TagBuilder<Body>
interface ThContentBuilder : TagContentBuilder<Body>
