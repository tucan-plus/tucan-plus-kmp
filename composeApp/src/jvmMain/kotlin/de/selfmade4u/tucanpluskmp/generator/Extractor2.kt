package de.selfmade4u.tucanpluskmp.generator

import java.util.PriorityQueue

object Extractor2 {

    sealed class MyHtml {
        data class Text(val text: String) : MyHtml()

        class Element(
            val name: String,
            val attributes: Map<String, String> = emptyMap(),
            val parent: Element?,
            childrenConstructor: (Element) -> List<MyHtml>,
        ) : MyHtml() {
            val children = childrenConstructor(this)

            fun nextSibling(): MyHtml? {
                return parent?.children?.getOrNull(parent.children.indexOf(this)+1)
            }
        }
    }

    data class ParseAttribute(val key: String, val value: Regex) {

    }

    sealed class ParsingInstruction {
        // Elements are null if done parsing
        data class ParsingReturn<T>(val parseNext: List<MyHtml?>, val value: T)

        abstract fun produceNextParsingSteps(htmls: List<MyHtml?>): ParsingReturn<List<ParsingInstruction>>

        // could be implemented in terms of the above function but that would likely be very inefficient
        abstract fun parsingProgress(htmls: List<MyHtml?>): ParsingReturn<Int>

        data class ParseText(val text: Regex) : ParsingInstruction() {
            override fun produceNextParsingSteps(htmls: List<MyHtml?>): ParsingReturn<List<ParsingInstruction>> {
                val new = htmls.mapAll { it as MyHtml.Text }

                if (new == null) {
                    check(false, { "Should never happen if not parsing incrementally" })
                    return ParsingReturn(htmls, emptyList())
                }

                return ParsingReturn(htmls.map { null }, listOf(this))
            }

            override fun parsingProgress(htmls: List<MyHtml?>): ParsingReturn<Int> {
                return if (htmls.all { it is MyHtml.Text }) {
                    ParsingReturn(htmls.map { null },1)
                } else {
                    ParsingReturn(htmls ,0)
                }
            }
        }

        data class ParseElement(val name: String, val attributes: List<ParseAttribute> = listOf(), val children: List<ParsingInstruction> = listOf()): ParsingInstruction() {
            private fun cartesianProduct(input: List<List<ParsingInstruction>>): List<List<ParsingInstruction>> {
                return input.fold(listOf(), { acc, elem ->
                    if (acc.isEmpty()) {
                        elem.map { listOf(it) }
                    } else {
                        acc.flatMap { accElem -> elem.map { e -> accElem + e } }
                    }
                })
            }

            override fun produceNextParsingSteps(htmls: List<MyHtml?>): ParsingReturn<List<ParsingInstruction>> {
                val new = htmls.mapAll { it as MyHtml.Element }

                if (new == null) {
                    check(false, { "Should never happen if not parsing incrementally" })
                    return ParsingReturn(htmls, emptyList())
                }

                // parse existing attributes and then check for remaining ones
                var htmlAttributes = new.map { it.attributes }

                for (attribute in attributes) {
                    htmlAttributes = htmlAttributes.map {
                        val value = it[attribute.key]
                        check(attribute.value.matches(value!!))
                        it.filterNot { i -> i.key == attribute.key && attribute.value.matches(i.value) }
                    }
                }
                if (!htmlAttributes.all { it.isEmpty() }) {
                    val nextAttributeKey = htmlAttributes.first().keys.first()

                    val newAttributeParser = ParseAttribute(nextAttributeKey, Regex(htmlAttributes.map { it.getValue(nextAttributeKey) }.toSet().map { "(" + Regex.escape(it) + ")" }.joinToString("|")))

                    return ParsingReturn(new.map { it.nextSibling() },listOf(this.copy(attributes = this.attributes + newAttributeParser)))
                }

                var newHtmls: List<MyHtml?> = htmls
                val newChildren: List<List<ParsingInstruction>> = children.map { child ->
                    // TODO this will get funny if we have state? like if conditionals?
                    val steps = child.produceNextParsingSteps(newHtmls)
                    newHtmls = steps.parseNext
                    // TODO replace product with this childs multiple options
                    // TODO maybe early return if more than 1 is returned
                    steps.value
                }
                val newChildrenPossibilities = cartesianProduct(newChildren)

                if (newChildrenPossibilities.size != 1) {
                    return ParsingReturn(new.map { it.nextSibling() },newChildrenPossibilities.map { newChildren -> this.copy(children = newChildren) })
                }

                val newChild = newHtmls.mapAll { it as MyHtml.Element }

                if (newChild != null) {
                    val newChildParser = ParseElement(name = newChild.map { it.name }.toSet().single())
                    return ParsingReturn(new.map { it.nextSibling() },listOf(this.copy(children = this.children + newChildParser)))
                } else {
                    return ParsingReturn(new.map { it.nextSibling() },listOf(this))
                }
            }

            override fun parsingProgress(htmls: List<MyHtml?>): ParsingReturn<Int> {
                val new = htmls.mapAll { it as MyHtml.Element }

                if (new == null) {
                    check(false, { "Should never happen if not parsing incrementally" })
                    return ParsingReturn(htmls, 0)
                }

                // parse existing attributes and then check for remaining ones
                var htmlAttributes = new.map { it.attributes }

                for (attribute in attributes) {
                    htmlAttributes = htmlAttributes.map {
                        val value = it[attribute.key]
                        check(attribute.value.matches(value!!))
                        it.filterNot { i -> i.key == attribute.key && attribute.value.matches(i.value) }
                    }
                }
                if (!htmlAttributes.all { it.isEmpty() }) {
                    return ParsingReturn(htmls, 1 + attributes.size)
                }

                var sum = 1 + attributes.size
                var newHtmls: List<MyHtml?> = htmls
                for (child in children) {
                    // oh no this needs to return the new html elements?
                    val step = child.parsingProgress(newHtmls)
                    newHtmls = step.parseNext
                    sum += step.value
                }
                return ParsingReturn(newHtmls, sum);
            }
        }
    }

    fun treesToParser(
        htmlTrees: List<MyHtml.Element>,
    ) {
        // do work here
        // for now assume that we always create the parsers from scratch and that the input html files don't change. this should make it much simpler

        // cache comparison value
        val workToDo = PriorityQueue<Pair<ParsingInstruction, Int>>({ a, b ->
            b.second.compareTo(a.second)
        })
        workToDo.add(ParsingInstruction.ParseElement("div", listOf(), listOf()).let { Pair(it, it.parsingProgress(htmlTrees).value) })

        while (!workToDo.isEmpty()) {
            val first = workToDo.remove().first;
            println("popping $first")

            // try adding a new element:
            // for efficiency the states should store where in the html they currently are and where in the html parsing state they currently are
            // that means both of these should be stored in an efficient way, because otherwise this traversal here will be very slow.
            // currently both could be linear right? which would make this much simpler? though at least for parsing a tree would help

            // maybe for simplicity first don't do this storing the state.

            // TODO we only need the progress here so if the steps would return the progress we don't need to calculate it twice
            val nextParsingSteps = first.produceNextParsingSteps(htmlTrees)
            for (parsingStep in nextParsingSteps.value) {
                println("adding $parsingStep")
                workToDo.add(parsingStep.let { Pair(it, it.parsingProgress(htmlTrees).value) })
            }
        }
    }
}

fun <I, O> List<I>.mapAll(function: (I) -> O): List<O>? {
    TODO("Not yet implemented")
}
