package de.selfmade4u

import java.util.PriorityQueue

import com.intellij.lang.annotation.AnnotationHolder
import com.intellij.lang.annotation.HighlightSeverity
import com.intellij.modcommand.ActionContext
import com.intellij.modcommand.ModPsiUpdater
import com.intellij.modcommand.Presentation
import com.intellij.modcommand.PsiUpdateModCommandAction
import com.intellij.openapi.diagnostic.logger
import com.intellij.openapi.diagnostic.thisLogger
import com.intellij.openapi.project.Project
import com.intellij.openapi.project.guessProjectDir
import com.intellij.openapi.vfs.findDirectory
import com.intellij.openapi.vfs.findPsiFile
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiWhiteSpace
import com.intellij.psi.impl.source.html.HtmlRawTextImpl
import com.intellij.psi.util.CachedValueProvider
import com.intellij.psi.util.CachedValuesManager
import com.intellij.psi.xml.*
import org.jetbrains.kotlin.analysis.api.analyze
import org.jetbrains.kotlin.analysis.api.resolution.KaCallInfo
import org.jetbrains.kotlin.analysis.api.resolution.KaFunctionCall
import org.jetbrains.kotlin.analysis.api.resolution.KaSuccessCallInfo
import org.jetbrains.kotlin.analysis.api.resolution.symbol
import org.jetbrains.kotlin.idea.base.util.projectScope
import org.jetbrains.kotlin.idea.stubindex.KotlinAnnotationsIndex
import org.jetbrains.kotlin.psi.*
import org.jetbrains.kotlin.psi.psiUtil.getNextSiblingIgnoringWhitespace
import org.jetbrains.kotlin.psi.psiUtil.getParentOfType
import org.jetbrains.kotlin.util.mapAll

object Extractor2 {

    sealed class MyHtml {
        data class Text(val text: String) : MyHtml()

        data class Element(
            val name: String,
            val attributes: Map<String, String> = emptyMap(),
            val children: List<MyHtml> = emptyList()
        ) : MyHtml()
    }

    class ParseAttribute() {

    }

    sealed class ParsingInstruction {
        class ParseText(val text: Regex) : ParsingInstruction() {
            override fun produceNextParsingSteps(htmls: List<MyHtml>): List<ParsingInstruction> {
                val new = htmls.mapAll { it as MyHtml.Text }

                if (new == null) {
                    return emptyList()
                }

                return listOf(this)
            }
        }

        class ParseElement(val name: String, val attributes: List<ParseAttribute>, val children: List<ParseElement>): ParsingInstruction() {
            override fun produceNextParsingSteps(htmls: List<MyHtml>): List<ParsingInstruction> {

            }
        }
        // Additional error types can be added here

        abstract fun produceNextParsingSteps(htmls: List<MyHtml>): List<ParsingInstruction>
    }

    fun myFun(
        project: Project,
        annotationEntry: KtAnnotationEntry
    ): CachedValueProvider.Result<MutableMap<PsiElement, Unit>> {
        val annotations = mutableMapOf<PsiElement, Unit>()
        val valueArg = annotationEntry.valueArgumentList!!.arguments.first()
        val path = Extractor.getStringLiteral(valueArg.stringTemplateExpression!!)
        thisLogger().warn("project dir ${project.guessProjectDir()} path $path");
        thisLogger().warn("htmls ${project.guessProjectDir()!!.findDirectory(path)}")
        val htmls = project.guessProjectDir()!!.findDirectory(path)!! // TODO FIXME error handling

        val ktNamedFunction = annotationEntry.getParentOfType<KtNamedFunction>(strict = true)!!
        //println("abc ${ktNamedFunction.text}")
        val block = ktNamedFunction.bodyBlockExpression!!

        val files = htmls.children
        thisLogger().warn("htmls2 ${files.map { (it.findPsiFile(project) as XmlFile).rootTag }}")
        val htmlFiles = files.map { (it.findPsiFile(project) as XmlFile).rootTag!! }

        val htmlTree1 = MyHtml.Element(
            name = "div",
            attributes = mapOf("class" to "container"),
            children = listOf(
                MyHtml.Element(
                    name = "p",
                    children = listOf(MyHtml.Text("Hello World"))
                )
            )
        )

        val htmlTree2 = MyHtml.Element(
            name = "div",
            attributes = mapOf("class" to "somethingelse"),
            children = listOf(
                MyHtml.Element(
                    name = "p",
                    children = listOf(MyHtml.Text("something else"))
                )
            )
        )

        // do work here
        // for now assume that we always create the parsers from scratch and that the input html files don't change. this should make it much simpler

        val workToDo = PriorityQueue<ParsingInstruction.ParseElement>()
        workToDo.add(ParsingInstruction.ParseElement("div", listOf(), listOf()))

        while (!workToDo.isEmpty()) {
            val first = workToDo.remove();

            // try adding a new element:
            // for efficiency the states should store where in the html they currently are and where in the html parsing state they currently are
            // that means both of these should be stored in an efficient way, because otherwise this traversal here will be very slow.
            // currently both could be linear right? which would make this much simpler? though at least for parsing a tree would help

            // maybe for simplicity first don't do this storing the state.

            val nextParsingSteps = first.produceNextParsingSteps(listOf(htmlTree1, htmlTree2))
        }

        return CachedValueProvider.Result(annotations, annotationEntry, htmls)
    }

    fun process(project: Project, annotationContext: PsiElement?, holder: AnnotationHolder?) {
        val annotations = KotlinAnnotationsIndex["HtmlFromResources", project, project.projectScope()];
        //println("annotations $annotations")
        for (annotationEntry in annotations) { // oh does the loop fail if one of them fails?
            // https://github.com/JetBrains/intellij-community/blob/master/platform/core-api/src/com/intellij/psi/util/CachedValue.java

            //ApplicationManager.getApplication().logError()

            try {
                val annotations =
                    CachedValuesManager.getManager(project).getCachedValue(annotationEntry) {
                        myFun(
                            project,
                            annotationEntry
                        )
                    }
                if (annotationContext != null && holder != null) {
                    annotations[annotationContext]?.let { info ->
                        holder.newAnnotation(HighlightSeverity.ERROR, info.message)
                            .range(annotationContext)
                            .apply { info.quickFix?.let { withFix(it) } }
                            .create()
                    }
                }
            } catch (error: Throwable) {
                LOG.error(error)
            }
        }
    }
}