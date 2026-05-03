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
import com.intellij.psi.util.CachedValueProvider
import com.intellij.psi.util.CachedValuesManager
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.psi.xml.XmlAttribute
import com.intellij.psi.xml.XmlElement
import com.intellij.psi.xml.XmlFile
import com.intellij.psi.xml.XmlTag
import com.intellij.psi.xml.XmlText
import com.intellij.psi.xml.XmlToken
import com.intellij.refactoring.extractMethod.newImpl.ExtractMethodHelper.addSiblingAfter
import com.intellij.util.concurrency.annotations.RequiresBackgroundThread
import org.jetbrains.kotlin.analysis.api.analyze
import org.jetbrains.kotlin.analysis.api.resolution.KaCallInfo
import org.jetbrains.kotlin.analysis.api.resolution.KaFunctionCall
import org.jetbrains.kotlin.analysis.api.resolution.KaSuccessCallInfo
import org.jetbrains.kotlin.analysis.api.resolution.symbol
import org.jetbrains.kotlin.idea.base.psi.replaced
import org.jetbrains.kotlin.idea.base.util.projectScope
import org.jetbrains.kotlin.idea.codeinsight.api.applicable.intentions.KotlinApplicableModCommandAction
import org.jetbrains.kotlin.idea.codeinsight.api.applicable.intentions.KotlinPsiUpdateModCommandAction
import org.jetbrains.kotlin.idea.stubindex.KotlinAnnotationsIndex
import org.jetbrains.kotlin.psi.KtAnnotationEntry
import org.jetbrains.kotlin.psi.KtBlockExpression
import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.psi.KtConstantExpression
import org.jetbrains.kotlin.psi.KtExpression
import org.jetbrains.kotlin.psi.KtFunction
import org.jetbrains.kotlin.psi.KtLambdaExpression
import org.jetbrains.kotlin.psi.KtNameReferenceExpression
import org.jetbrains.kotlin.psi.KtNamedFunction
import org.jetbrains.kotlin.psi.KtProperty
import org.jetbrains.kotlin.psi.KtPsiFactory
import org.jetbrains.kotlin.psi.KtStringTemplateExpression
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
class MyQuickFix(element: KtExpression, val expression: String) : PsiUpdateModCommandAction<KtExpression>(element) {

    override fun getFamilyName(): String = "My Plugin Fixes"

    override fun getPresentation(context: ActionContext, element: KtExpression): Presentation {
        return Presentation.of("Fix the html parsing")
    }

    override fun invoke(context: ActionContext, element: KtExpression, updater: ModPsiUpdater) {
        element.addSiblingAfter(KtPsiFactory(context.project).createExpression(expression))
        element.addSiblingAfter(KtPsiFactory(context.project).createNewLine())
    }
}

object Extractor {

    fun getStringLiteral(expression: KtStringTemplateExpression): String {
        check(!expression.hasInterpolation())
        return expression.entries.single().text
    }

    // the ktexpression is the one that has been parsed and after which to add new code
    fun checkExpression(annotations: MutableMap<PsiElement, AnnotationResult>, expression: KtExpression, htmlElement: XmlElement): Pair<XmlElement, KtExpression> {
        //println("statement ${statement.text}")
        // https://kotlin.github.io/analysis-api/fundamentals.html#kalifetimeowner
        when (expression) {
            is KtLambdaExpression -> {
                return checkExpression(annotations, expression.bodyExpression!!, htmlElement)
            }
            is KtBlockExpression -> {
                var htmlTag: Pair<XmlElement, KtExpression> = htmlElement to expression
                for (statement in expression.statements) {
                    htmlTag = checkExpression(annotations, statement, htmlTag.first)
                }
                return htmlTag
            }
            is KtCallExpression -> {
                analyze(expression) {
                    val resolveToCall: KaCallInfo? = expression.resolveToCall() // sealed class, can get a Ka(Function)Call
                    when (resolveToCall) {
                        is KaSuccessCallInfo -> {
                            val psi = (resolveToCall.call as KaFunctionCall<*>).symbol.psi

                            val fqName = (resolveToCall.call as KaFunctionCall<*>).symbol.callableId!!.asSingleFqName().asString()
                            when (fqName) {
                                "de.selfmade4u.tucanpluskmp.doctype" -> {
                                    annotations[expression] = AnnotationResult("Deprecated. Doctype does not need to be parsed.")
                                    return htmlElement to expression
                                }
                                "de.selfmade4u.tucanpluskmp.html", "de.selfmade4u.tucanpluskmp.head" -> {
                                    val tag = fqName.split(".").last()
                                    if (htmlElement is XmlTag && htmlElement.name == tag) {
                                        println("matched html tag")
                                        var next = htmlElement.firstChild
                                        do {
                                            next = next.nextSibling
                                        } while (next is PsiWhiteSpace || next is XmlToken || (next is XmlText && next.text.trim().isEmpty()))
                                        val htmlElement = checkExpression(annotations, expression.valueArguments.single().getArgumentExpression()!!, next as XmlElement)
                                        return htmlElement
                                    } else {
                                        annotations[expression] = AnnotationResult("expected <$tag> but found ${htmlElement::class}")
                                        return htmlElement to expression
                                    }
                                }
                                "de.selfmade4u.tucanpluskmp.HtmlTag.attribute" -> {
                                    if (htmlElement is XmlAttribute) {
                                        println("matched attribute")
                                        check(expression.valueArguments.size == 2)
                                        val (first, second) = expression.valueArguments
                                        if (htmlElement.name != getStringLiteral(first.stringTemplateExpression!!)) {
                                            annotations[expression] = AnnotationResult("attribute name actual ${htmlElement.name} does not match expected ${getStringLiteral(first.stringTemplateExpression!!)}")
                                        }
                                        if (htmlElement.value != getStringLiteral(second.stringTemplateExpression!!)) {
                                            annotations[expression] = AnnotationResult("attribute value actual ${htmlElement.value} does not match expected ${getStringLiteral(second.stringTemplateExpression!!)}")
                                        }
                                        // here also xmltext which is empty needs to be skipped?
                                        var next: PsiElement = htmlElement
                                        do {
                                            next = next.nextSibling
                                        } while (next is PsiWhiteSpace || next is XmlToken || (next is XmlText && next.text.trim().isEmpty()))
                                        //val next = PsiTreeUtil.skipSiblingsForward(htmlElement, PsiWhiteSpace::class.java, XmlToken::class.java) as XmlElement
                                        return next as XmlElement to expression
                                    } else {
                                        annotations[expression] = AnnotationResult("expected attribute but found ${htmlElement::class}")
                                        return htmlElement to expression
                                    }
                                }
                                else -> {
                                    val implementation = psi as? KtFunction

                                    if (implementation != null) {
                                        annotations[expression] = AnnotationResult("TOOD implementation $fqName needs analysis")
                                    } else {
                                        annotations[expression] = AnnotationResult("TODO unknown called method $fqName")
                                    }
                                    return htmlElement to expression
                                }
                            }
                        }
                        else -> {
                            annotations[expression] = AnnotationResult("failed to resolve")
                            return htmlElement to expression
                        }
                    }
                }
            }
            is KtProperty -> {
                val initializer = expression.initializer
                return checkExpression(annotations, initializer!!, htmlElement)
            }
            is KtStringTemplateExpression -> {
                var htmlElement: Pair<XmlElement, KtExpression> = htmlElement to expression
                for (entry in expression.entries) {
                    entry.expression?.let {
                        htmlElement = checkExpression(annotations, it, htmlElement.first)
                    }
                }
                return htmlElement
            }
            is KtConstantExpression -> {
                // fine
                return htmlElement to expression
            }
            is KtNameReferenceExpression -> {
                // variable reference
                // maybe KtSimpleNameExpression
                return htmlElement to expression
            }
            else -> {
                annotations[expression] = AnnotationResult("Unknown HTML parser statement ${expression::class}")
                return htmlElement to expression
            }
        }
    }

    data class AnnotationResult(val message: String, val quickFix: MyQuickFix? = null)

    // https://github.com/JetBrains/intellij-community/blob/b926099be855e2e1c34d21df1e496f29ecbe7f52/platform/core-impl/src/com/intellij/util/CachedValueStabilityChecker.java#L62
    fun myFun(project: Project, annotationEntry: KtAnnotationEntry): CachedValueProvider.Result<MutableMap<PsiElement, AnnotationResult>> {
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
            // TODO here we should start parsing htmlTag
            val parsedUntil = checkExpression(annotations, block, htmlFile)
            // TODO produce quickfix
            println("parsedUntil $parsedUntil")
            if (parsedUntil.first is XmlTag) {
                // TODO FIXME I think persisting PsiElements like this is not allowed
                annotations[parsedUntil.second] = AnnotationResult("Fix the parsing here", MyQuickFix(parsedUntil.second, "${(parsedUntil.first as XmlTag).name} {}"))
            } else if (parsedUntil.first is XmlText) {
                annotations[parsedUntil.second] = AnnotationResult("Fix the parsing here", MyQuickFix(parsedUntil.second, "extractText()"))
            } else {
                annotations[parsedUntil.second] = AnnotationResult("Failed to parse the rest but can't autofix")
                annotations[parsedUntil.first] = AnnotationResult("Remaining part to parse")
            }
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
