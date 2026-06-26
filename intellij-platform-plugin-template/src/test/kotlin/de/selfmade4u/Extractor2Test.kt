package de.selfmade4u

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class Extractor2Test {

    @Test
    fun testtreesToParser() {
        val htmlTree1 = Extractor2.MyHtml.Element(
            name = "div",
            attributes = mapOf("class" to "container"),
            children = listOf(
                Extractor2.MyHtml.Element(
                    name = "p",
                    children = listOf(Extractor2.MyHtml.Text("Hello World"))
                )
            )
        )

        val htmlTree2 = Extractor2.MyHtml.Element(
            name = "div",
            attributes = mapOf("class" to "somethingelse"),
            children = listOf(
                Extractor2.MyHtml.Element(
                    name = "p",
                    children = listOf(Extractor2.MyHtml.Text("something else"))
                )
            )
        )

        val result = Extractor2.treesToParser(listOf(htmlTree1, htmlTree2))

    }
}