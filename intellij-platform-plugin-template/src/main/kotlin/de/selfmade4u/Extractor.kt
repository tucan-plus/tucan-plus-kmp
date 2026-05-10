package de.selfmade4u

import com.intellij.lang.annotation.AnnotationHolder
import com.intellij.lang.annotation.HighlightSeverity
import com.intellij.modcommand.ActionContext
import com.intellij.modcommand.ModPsiUpdater
import com.intellij.modcommand.Presentation
import com.intellij.modcommand.PsiUpdateModCommandAction
import com.intellij.openapi.project.Project
import com.intellij.openapi.project.guessProjectDir
import com.intellij.openapi.vfs.findPsiFile
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiWhiteSpace
import com.intellij.psi.impl.source.html.HtmlRawTextImpl
import com.intellij.psi.util.CachedValueProvider
import com.intellij.psi.util.CachedValuesManager
import com.intellij.psi.xml.*
import com.intellij.refactoring.extractMethod.newImpl.ExtractMethodHelper.addSiblingAfter
import org.jetbrains.kotlin.analysis.api.analyze
import org.jetbrains.kotlin.analysis.api.components.expressionType
import org.jetbrains.kotlin.analysis.api.resolution.KaCallInfo
import org.jetbrains.kotlin.analysis.api.resolution.KaFunctionCall
import org.jetbrains.kotlin.analysis.api.resolution.KaSuccessCallInfo
import org.jetbrains.kotlin.analysis.api.resolution.symbol
import org.jetbrains.kotlin.idea.base.util.projectScope
import org.jetbrains.kotlin.idea.stubindex.KotlinAnnotationsIndex
import org.jetbrains.kotlin.idea.util.CommentSaver.Companion.tokenType
import org.jetbrains.kotlin.psi.*
import org.jetbrains.kotlin.psi.psiUtil.getParentOfType

// https://github.com/JetBrains/kotlin/blob/master/analysis/docs/contribution-guide/api-development.md
// https://youtrack.jetbrains.com/issue/KT-61404/Analysis-API-implement-proper-library-publishing-structure
// lots of moving parts (assuming this work ever gets done)

// https://github.com/JetBrains/intellij-community/blob/37b233a6b70fb606dad5f13eb001c63fb6b12cf4/plugins/kotlin/base/analysis/src/org/jetbrains/kotlin/idea/stubindex/IdeStubIndexService.java#L306
// https://github.com/JetBrains/kotlin/blob/a66400f13ad48df4ed889ba98d94c0ece05d9acf/compiler/psi/psi-impl/src/org/jetbrains/kotlin/psi/stubs/elements/StubIndexService.kt#L12

// my first task should be to extract the information from the kotlin html parsing code.
// then we can try to generate autofixes in all directions

// https://github.com/JetBrains/intellij-community/blob/91a83ad51b25c1f4e8c95abed95fe9fac117caac/plugins/kotlin/docs/fir-ide/architecture/code-insights.md

// https://docs.google.com/document/d/1-2_cNjq-Mc28j0eCX1TEuMM-k6UXKvfPTutvIBafIJA/edit?pli=1&tab=t.0#heading=h.z5lwn79yvfdm
// https://github.com/JetBrains/intellij-community/blob/91a83ad51b25c1f4e8c95abed95fe9fac117caac/plugins/kotlin/code-insight/api/src/org/jetbrains/kotlin/idea/codeinsight/api/applicable/intentions/KotlinPsiUpdateModCommandAction.kt#L12
// expression into which to insert at the end?
class MyQuickFix(element: KtExpression, val expression: String) : PsiUpdateModCommandAction<KtExpression>(element) {

    override fun getFamilyName(): String = "My Plugin Fixes"

    override fun getPresentation(context: ActionContext, element: KtExpression): Presentation {
        return Presentation.of("Fix the html parsing")
    }

    override fun invoke(context: ActionContext, element: KtExpression, updater: ModPsiUpdater) {
        element.add(KtPsiFactory(context.project).createExpression(expression))
        element.add(KtPsiFactory(context.project).createNewLine())
    }
}

object Extractor {

    fun getStringLiteral(expression: KtStringTemplateExpression): String {
        check(!expression.hasInterpolation())
        return expression.entries.single().text
    }

    /**
     * Pass the kotlin expression that should parse the passed html element. Returns a pair of which xml element should be parsed next (as nothing may have been parsed) and which kotlin expression should do that.
     */
    fun checkExpression(
        annotations: MutableMap<PsiElement, AnnotationResult>,
        expression: KtExpression,
        htmlElement: XmlElement
    ): XmlElement {
        //println("statement ${statement.text}")
        // https://kotlin.github.io/analysis-api/fundamentals.html#kalifetimeowner
        when (expression) {
            is KtLambdaExpression -> {
                return checkExpression(annotations, expression.bodyExpression!!, htmlElement)
            }

            is KtBlockExpression -> {
                var htmlTag: XmlElement = htmlElement
                for (statement in expression.statements) {
                    htmlTag = checkExpression(annotations, statement, htmlTag)
                    while (htmlTag is XmlToken && htmlTag.tokenType == XmlTokenType.XML_TAG_END) {
                        htmlTag = htmlTag.nextSibling as XmlElement
                    }
                    // skip xml tag end here?
                }
                return htmlTag
            }
            is KtDotQualifiedExpression -> {
                val receiverExpression = expression.receiverExpression
                val selectorExpression = expression.selectorExpression
                println("selector ${receiverExpression::class}")
                println("receiver ${selectorExpression!!::class}")
                val name: KtNameReferenceExpression
                val attributes: KtCallExpression?
                val content: KtCallExpression?
                if (receiverExpression is KtDotQualifiedExpression && selectorExpression is KtCallExpression) {
                    name = receiverExpression.receiverExpression as KtNameReferenceExpression
                    attributes = receiverExpression.selectorExpression as KtCallExpression
                    content = selectorExpression
                } else if (receiverExpression is KtNameReferenceExpression && selectorExpression is KtCallExpression) {
                    name = receiverExpression
                    val whatIsCalled =
                        (selectorExpression.calleeExpression as KtNameReferenceExpression).getReferencedName()
                    when (whatIsCalled) {
                        "attributes" -> {
                            attributes = selectorExpression
                            content = null
                        }

                        "content" -> {
                            attributes = null
                            content = selectorExpression
                        }

                        else -> {
                            annotations[expression] = AnnotationResult("unknown call $whatIsCalled")
                            return htmlElement
                        }
                    }
                } else {
                    annotations[expression] = AnnotationResult("Unknown chained call ${expression::class}")
                    return htmlElement
                }
                println("name ${name.text} attributes ${attributes?.text} content ${content?.text}")
                /*else {
                    // receiverExpression "html"
                    // selectorExpression .content {}
                    analyze(receiverExpression) {
                        // de/selfmade4u/tucanpluskmp/HtmlBuilder
                        println(receiverExpression.expressionType)
                    }
                    analyze(selectorExpression) {
                        val resolveToCall: KaCallInfo? =
                            selectorExpression.resolveToCall() // sealed class, can get a Ka(Function)Call
                        when (resolveToCall) {
                            is KaSuccessCallInfo -> {
                                val psi = (resolveToCall.call as KaFunctionCall<*>).symbol.psi

                                val fqName = (resolveToCall.call as KaFunctionCall<*>).symbol.callableId!!.asSingleFqName()
                                    .asString()
                                when (fqName) {
                                    "de.selfmade4u.tucanpluskmp.attributes" -> {
                                        println("TODO handle attributes")
                                    }
                                    "de.selfmade4u.tucanpluskmp.content" -> {
                                        println("TODO handle content")
                                    }
                                    "de.selfmade4u.tucanpluskmp.html", "de.selfmade4u.tucanpluskmp.head", "de.selfmade4u.tucanpluskmp.title", "de.selfmade4u.tucanpluskmp.meta" -> {
                                        val tag = receiverExpression.text
                                        if (htmlElement is XmlTag && htmlElement.name == tag) {
                                            println("matched html tag")
                                            var htmlElement: PsiElement = htmlElement.firstChild
                                            while (htmlElement is PsiWhiteSpace) {
                                                htmlElement = htmlElement.nextSibling
                                            }
                                            check((htmlElement as XmlToken).tokenType == XmlTokenType.XML_START_TAG_START)
                                            do {
                                                htmlElement = htmlElement.nextSibling
                                            } while (htmlElement is PsiWhiteSpace)
                                            check((htmlElement as XmlToken).tokenType == XmlTokenType.XML_NAME)
                                            do {
                                                htmlElement = htmlElement.nextSibling
                                            } while (htmlElement is PsiWhiteSpace || (htmlElement is XmlText && htmlElement.text.trim()
                                                    .isEmpty())
                                            )
                                            val expr = selectorExpression.valueArguments.single()
                                                .getArgumentExpression()!! as KtLambdaExpression
                                            htmlElement = checkExpression(annotations, expr, htmlElement as XmlElement)
                                            println("ABC $htmlElement")
                                            while (htmlElement is PsiWhiteSpace || htmlElement is XmlText && htmlElement.text.trim()
                                                    .isEmpty()) {
                                                htmlElement = htmlElement.nextSibling
                                            }
                                            when (htmlElement) {
                                                is XmlToken if htmlElement.tokenType == XmlTokenType.XML_TAG_END -> {
                                                    println("found closing tag")
                                                    htmlElement = htmlElement.nextSibling
                                                }

                                                is XmlTag -> {
                                                    annotations[expr.rightCurlyBrace!! as PsiElement] = AnnotationResult(
                                                        "Fix the parsing here 1",
                                                        MyQuickFix(
                                                            expr.bodyExpression as KtExpression,
                                                            "${(htmlElement).name} {}"
                                                        )
                                                    )
                                                }

                                                is XmlText -> {
                                                    annotations[expr.rightCurlyBrace!! as PsiElement] =
                                                        AnnotationResult(
                                                            "Fix the parsing here 2",
                                                            MyQuickFix(
                                                                expr.bodyExpression as KtExpression,
                                                                "extractText()"
                                                            )
                                                        )
                                                }

                                                is HtmlRawTextImpl -> {
                                                    annotations[expr.rightCurlyBrace!! as PsiElement] =
                                                        AnnotationResult(
                                                            "Fix the parsing here 3",
                                                            MyQuickFix(
                                                                expr.bodyExpression as KtExpression,
                                                                "extractText()"
                                                            )
                                                        )
                                                }

                                                else -> {
                                                    annotations[expr.rightCurlyBrace!! as PsiElement] =
                                                        AnnotationResult("Failed to parse the rest but can't autofix $htmlElement ${htmlElement.text}")
                                                    annotations[htmlElement] = AnnotationResult("Remaining part to parse")
                                                }
                                            }
                                            return htmlElement as XmlElement
                                        } else {
                                            annotations[selectorExpression] =
                                                AnnotationResult("expected <$tag> but found ${htmlElement::class}")
                                            return htmlElement
                                        }
                                    }
                                    else -> {
                                        TODO(fqName)
                                        val implementation = psi as? KtFunction

                                        if (implementation != null) {
                                            annotations[expression] =
                                                AnnotationResult("TOOD implementation $fqName needs analysis")
                                        } else {
                                            annotations[expression] = AnnotationResult("TODO unknown called method $fqName")
                                        }
                                        return htmlElement
                                    }
                                }
                            }

                            else -> {
                                annotations[expression] = AnnotationResult("failed to resolve")
                                return htmlElement
                            }
                        }
                    }*/

                println(expression.selectorExpression)
                println(expression.receiverExpression)
                return htmlElement
            }
            is KtCallExpression -> {
                val selectorExpression = expression
                analyze(selectorExpression) {
                    val resolveToCall: KaCallInfo? =
                        selectorExpression.resolveToCall() // sealed class, can get a Ka(Function)Call
                    when (resolveToCall) {
                        is KaSuccessCallInfo -> {
                            val psi = (resolveToCall.call as KaFunctionCall<*>).symbol.psi

                            val fqName = (resolveToCall.call as KaFunctionCall<*>).symbol.callableId!!.asSingleFqName()
                                .asString()
                            when (fqName) {
                                "de.selfmade4u.tucanpluskmp.HtmlTag.attribute" -> {
                                    if (htmlElement is XmlAttribute) {
                                        println("matched attribute")
                                        check(selectorExpression.valueArguments.size == 2)
                                        val (first, second) = selectorExpression.valueArguments
                                        if (htmlElement.name != getStringLiteral(first.stringTemplateExpression!!)) {
                                            annotations[selectorExpression] = AnnotationResult(
                                                "attribute name actual ${htmlElement.name} does not match expected ${
                                                    getStringLiteral(first.stringTemplateExpression!!)
                                                }"
                                            )
                                        }
                                        if (htmlElement.value != getStringLiteral(second.stringTemplateExpression!!)) {
                                            annotations[selectorExpression] = AnnotationResult(
                                                "attribute value actual ${htmlElement.value} does not match expected ${
                                                    getStringLiteral(second.stringTemplateExpression!!)
                                                }"
                                            )
                                        }
                                        var next: PsiElement = htmlElement
                                        do {
                                            next = next.nextSibling
                                        } while (next is PsiWhiteSpace || (next is XmlToken && next.tokenType == XmlTokenType.XML_TAG_END) || (next is XmlText && next.text.trim()
                                                .isEmpty())
                                        )
                                        return next as XmlElement
                                    } else {
                                        annotations[selectorExpression] =
                                            AnnotationResult("expected attribute but found ${htmlElement::class}")
                                        return htmlElement
                                    }
                                }

                                "de.selfmade4u.tucanpluskmp.HtmlTag.extractText" -> {
                                    if (htmlElement is HtmlRawTextImpl || htmlElement is XmlText) {
                                        var next: PsiElement = htmlElement
                                        do {
                                            if (next.nextSibling == null) {
                                                // return so caller can close element? this is technically wrong as we seem to want to return which element we want to parse next not which one we just parsed? maybe change that?
                                                return next as XmlElement
                                            } else {
                                                next = next.nextSibling
                                            }
                                        } while (next is PsiWhiteSpace || (next is XmlText && next.text.trim()
                                                .isEmpty())
                                        )
                                        return next as XmlElement
                                    } else {
                                        annotations[selectorExpression] =
                                            AnnotationResult("expected text but found ${htmlElement::class}")
                                        return htmlElement
                                    }
                                }

                                else -> {
                                    TODO(fqName)
                                    val implementation = psi as? KtFunction

                                    if (implementation != null) {
                                        annotations[expression] =
                                            AnnotationResult("TOOD implementation $fqName needs analysis")
                                    } else {
                                        annotations[expression] = AnnotationResult("TODO unknown called method $fqName")
                                    }
                                    return htmlElement
                                }
                            }
                        }

                        else -> {
                            annotations[expression] = AnnotationResult("failed to resolve")
                            return htmlElement
                        }
                    }
                }
            }

            is KtProperty -> {
                val initializer = expression.initializer
                return checkExpression(annotations, initializer!!, htmlElement)
            }

            is KtStringTemplateExpression -> {
                var htmlElement: XmlElement = htmlElement
                for (entry in expression.entries) {
                    entry.expression?.let {
                        htmlElement = checkExpression(annotations, it, htmlElement)
                    }
                }
                return htmlElement
            }

            is KtConstantExpression -> {
                // fine
                return htmlElement
            }

            is KtNameReferenceExpression -> {
                // variable reference
                // maybe KtSimpleNameExpression
                return htmlElement
            }
            else -> {
                annotations[expression] = AnnotationResult("Unknown HTML parser statement ${expression::class}")
                return htmlElement
            }
        }
    }

    data class AnnotationResult(val message: String, val quickFix: MyQuickFix? = null)

    // https://github.com/JetBrains/intellij-community/blob/b926099be855e2e1c34d21df1e496f29ecbe7f52/platform/core-impl/src/com/intellij/util/CachedValueStabilityChecker.java#L62
    fun myFun(
        project: Project,
        annotationEntry: KtAnnotationEntry
    ): CachedValueProvider.Result<MutableMap<PsiElement, AnnotationResult>> {
        val annotations = mutableMapOf<PsiElement, AnnotationResult>()
        val valueArg = annotationEntry.valueArgumentList!!.arguments.first()
        val path = getStringLiteral(valueArg.stringTemplateExpression!!)
        val htmls = project.guessProjectDir()!!.findFileByRelativePath(path)!!

        val ktNamedFunction = annotationEntry.getParentOfType<KtNamedFunction>(strict = true)!!
        //println("abc ${ktNamedFunction.text}")
        val block = ktNamedFunction.bodyBlockExpression!!

        val files = htmls.children
        val htmlFiles = files.map { (it.findPsiFile(project) as XmlFile).rootTag!! }

        println("$htmlFiles")
        for (htmlFile in htmlFiles) {
            val parsedUntil = checkExpression(annotations, block, htmlFile)
        }
        return CachedValueProvider.Result(annotations, annotationEntry, htmls)
    }

    fun process(project: Project, annotationContext: PsiElement?, holder: AnnotationHolder?) {
        val annotations = KotlinAnnotationsIndex["HtmlFromResources", project, project.projectScope()];
        //println("annotations $annotations")
        for (annotationEntry in annotations) {
            // https://github.com/JetBrains/intellij-community/blob/master/platform/core-api/src/com/intellij/psi/util/CachedValue.java

            val annotations = CachedValuesManager.getManager(project).getCachedValue(annotationEntry) {
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
        }
    }
}

// we probably want to be able to calculate how far the parsing code is executed for each html file
// the one that goes the shortest needs fixing at that place in the source code

// some change examples then can be tested against all files and check whether we make more progress in the parsing code
// and if we do there if we make more progress in the html code? maybe this is a 1:1 mapping if we count loops.
