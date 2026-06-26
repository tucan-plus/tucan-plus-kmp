package de.selfmade4u.tucanpluskmp.de.selfmade4u.tucanpluskmp.generator

import de.selfmade4u.tucanpluskmp.generator.Extractor2
import kotlin.test.Test

class Extractor2Test {

    @Test
    fun testtreesToParser() {
        val htmlTree1 = Extractor2.MyHtml.Element(
            name = "div",
            attributes = mapOf("class" to "container"),
            parent = null,
            childrenConstructor = { parent -> listOf(
                Extractor2.MyHtml.Element(
                    name = "p",
                    parent = parent,
                    childrenConstructor = { parent -> listOf(Extractor2.MyHtml.Text("Hello World", parent)) }
                )
            )}
        )

        val htmlTree2 = Extractor2.MyHtml.Element(
            name = "div",
            attributes = mapOf("class" to "somethingelse"),
            parent = null,
            childrenConstructor = { parent -> listOf(
                Extractor2.MyHtml.Element(
                    name = "p",
                    parent = parent,
                    childrenConstructor = { parent -> listOf(Extractor2.MyHtml.Text("something else", parent)) }
                )
            )}
        )

        val result = Extractor2.treesToParser(listOf(htmlTree1, htmlTree2))

    }
}