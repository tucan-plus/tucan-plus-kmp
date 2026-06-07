package de.selfmade4u

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

// https://github.com/JetBrains/kotlin/blob/master/analysis/docs/contribution-guide/api-development.md
// https://youtrack.jetbrains.com/issue/KT-61404/Analysis-API-implement-proper-library-publishing-structure
// lots of moving parts (assuming this work ever gets done)

// https://github.com/JetBrains/intellij-community/blob/37b233a6b70fb606dad5f13eb001c63fb6b12cf4/plugins/kotlin/base/analysis/src/org/jetbrains/kotlin/idea/stubindex/IdeStubIndexService.java#L306
// https://github.com/JetBrains/kotlin/blob/a66400f13ad48df4ed889ba98d94c0ece05d9acf/compiler/psi/psi-impl/src/org/jetbrains/kotlin/psi/stubs/elements/StubIndexService.kt#L12

// my first task should be to extract the information from the kotlin html parsing code.
// then we can try to generate autofixes in all directions

// https://github.com/JetBrains/intellij-community/blob/91a83ad51b25c1f4e8c95abed95fe9fac117caac/plugins/kotlin/docs/fir-ide/architecture/code-insights.md

private val XmlElement.nextInterestingSibling: XmlElement?
    get() {
        var next: PsiElement? = this
        do {
            next = next?.nextSibling
        } while (next is PsiWhiteSpace || next is XmlText && next.value.trim()
                .isEmpty() || next is XmlToken
        )
        return next as XmlElement?
    }

private val XmlElement.firstInterestingChild: XmlElement?
    get() {
        var next: PsiElement? = this.firstChild
         while (next is PsiWhiteSpace || next is XmlText && next.value.trim()
                .isEmpty() || next is XmlToken || next is XmlAttribute
        ) {
             next = next.nextSibling
         }
        return next as XmlElement?
    }

// https://docs.google.com/document/d/1-2_cNjq-Mc28j0eCX1TEuMM-k6UXKvfPTutvIBafIJA/edit?pli=1&tab=t.0#heading=h.z5lwn79yvfdm
// https://github.com/JetBrains/intellij-community/blob/91a83ad51b25c1f4e8c95abed95fe9fac117caac/plugins/kotlin/code-insight/api/src/org/jetbrains/kotlin/idea/codeinsight/api/applicable/intentions/KotlinPsiUpdateModCommandAction.kt#L12
// expression into which to insert at the end?
class MyQuickFixAddToEndOfBlock(element: KtBlockExpression, val expression: String) : PsiUpdateModCommandAction<KtBlockExpression>(element) {

    override fun getFamilyName(): String = "My Plugin Fixes 1"

    override fun getPresentation(context: ActionContext, element: KtBlockExpression): Presentation {
        return Presentation.of("Fix the html parsing 1")
    }

    override fun invoke(context: ActionContext, element: KtBlockExpression, updater: ModPsiUpdater) {
        element.addBefore(KtPsiFactory(context.project).createNewLine(), element.rBrace)
        element.addBefore(KtPsiFactory(context.project).createExpression(expression), element.rBrace)
    }
}

class MyQuickFixAddContentCall(element: KtCallExpression) : PsiUpdateModCommandAction<KtCallExpression>(element) {

    override fun getFamilyName(): String = "My Plugin Fixes 2"

    override fun getPresentation(context: ActionContext, element: KtCallExpression): Presentation {
        return Presentation.of("Fix the html parsing 2")
    }

    override fun invoke(context: ActionContext, element: KtCallExpression, updater: ModPsiUpdater) {
        val new = KtPsiFactory(context.project).buildExpression {
            appendExpressions(listOf(element, KtPsiFactory(context.project).createExpression("content {}")), ".")
        }
        element.replace(new)
    }
}


object Extractor {
    private val LOG = logger<Extractor>()

    fun getStringLiteral(expression: KtStringTemplateExpression): String {
        check(!expression.hasInterpolation())
        return expression.entries.single().text
    }

    /**
     * Pass the kotlin expression that should parse the passed html element. Returns a pair of which xml element should be parsed next (as nothing may have been parsed).
     */
    fun checkExpression(
        annotations: MutableMap<PsiElement, AnnotationResult>,
        expression: KtExpression,
        htmlElement: XmlElement
    ): XmlElement? {
        //println("statement ${statement.text}")
        // https://kotlin.github.io/analysis-api/fundamentals.html#kalifetimeowner
        when (expression) {
            is KtLambdaExpression -> {
                return checkExpression(annotations, expression.bodyExpression!!, htmlElement)
            }

            is KtBlockExpression -> {
                var htmlTag: XmlElement = htmlElement
                for (statement in expression.statements) {
                    val htmlTagTmp = checkExpression(annotations, statement, htmlTag)
                    if (htmlTagTmp == null) {
                        if (expression.statements.last() != statement) {
                            annotations[statement] = AnnotationResult("Superfluous calls after this call")
                        }
                        return null
                    }
                    htmlTag = htmlTagTmp
                }
                return htmlTag
            }
            is KtDotQualifiedExpression -> {
                // we should be able to detect attributes and content at once so either we've fully parsed or not
                val receiverExpression = expression.receiverExpression
                val selectorExpression = expression.selectorExpression
                //println("selector ${receiverExpression::class}")
                //println("receiver ${selectorExpression!!::class}")
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
                //println("name ${name.text} attributes ${attributes?.text} content ${content?.text}")
                val tag = name.getReferencedName()
                if (htmlElement is XmlTag && htmlElement.name == tag) {
                    val tagElement = htmlElement
                    //println("matched $tag tag")
                    var currentAttribute: XmlElement? = htmlElement.attributes.firstOrNull()
                    attributes?.let { attributes ->
                        val expr = attributes.valueArguments.single()
                            .getArgumentExpression()!! as KtLambdaExpression
                        currentAttribute = checkExpression(annotations, expr, currentAttribute!!)
                    }
                    //println("currentAttribute $currentAttribute")
                    if (currentAttribute != null) {
                        annotations[attributes!!] = AnnotationResult("Unparsed attribute",
                            MyQuickFixAddToEndOfBlock((attributes.valueArguments.single()
                                .getArgumentExpression()!! as KtLambdaExpression).bodyExpression!!, "attribute(\"${(currentAttribute as XmlAttribute).name}\", \"${currentAttribute.value}\")"))
                    }
                    var currentChild = tagElement.firstInterestingChild
                    content?.let { content ->
                        if (currentChild == null) {
                            annotations[content] = AnnotationResult("Superfluous calls after this call")
                            return htmlElement.nextInterestingSibling
                        }
                        val expr = content.valueArguments.single()
                            .getArgumentExpression()!! as KtLambdaExpression
                        currentChild = checkExpression(annotations, expr, currentChild)
                    }
                    //println("currentchild $currentChild")
                    when (currentChild) {
                        is XmlTag -> {
                            annotations[htmlElement] = AnnotationResult("Unparsed element")
                            if (content != null) {
                                val expr = content.valueArguments.single()
                                    .getArgumentExpression()!! as KtLambdaExpression
                                if (currentChild.attributes.isEmpty()) {
                                    annotations[expression] = AnnotationResult(
                                        "Here more content parsing is needed", MyQuickFixAddToEndOfBlock(
                                            expr.bodyExpression!!,
                                            "${(currentChild).name}.content {}"
                                        )
                                    )
                                } else {
                                    annotations[expression] = AnnotationResult(
                                        "Here more attribute parsing is needed", MyQuickFixAddToEndOfBlock(
                                            expr.bodyExpression!!,
                                            "${(currentChild).name}.attributes {}"
                                        )
                                    )
                                }
                            } else {
                                annotations[expression] = AnnotationResult(
                                    "Here more content parsing is needed", MyQuickFixAddContentCall(attributes!!)
                                )
                            }
                        }
                        is HtmlRawTextImpl, is XmlText -> {
                            if (content != null) {
                                val expr = content.valueArguments.single()
                                    .getArgumentExpression()!! as KtLambdaExpression
                                annotations[expression] = AnnotationResult(
                                    "Here text would need to be parsed", MyQuickFixAddToEndOfBlock(
                                        expr.bodyExpression!!,
                                        "extractText()"
                                    )
                                )
                            } else {
                                annotations[expression] = AnnotationResult(
                                    "Here more content parsing is needed",
                                    MyQuickFixAddContentCall(attributes!!)
                                )
                            }
                        }
                        null -> {
                            // done parsing
                        }
                        else -> {
                            check(false)
                        }
                    }
                } else {
                    annotations[expression] = AnnotationResult("fail 3")
                }
                return htmlElement.nextInterestingSibling
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
                                "de.selfmade4u.tucanpluskmp.HtmlAttributeScope.attribute" -> {
                                    if (htmlElement is XmlAttribute) {
                                        //println("matched attribute")
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
                                        val afterAttribute = htmlElement.getNextSiblingIgnoringWhitespace()
                                        //println("afterAttribute $afterAttribute")
                                        return afterAttribute as? XmlAttribute
                                    } else {
                                        annotations[selectorExpression] =
                                            AnnotationResult("expected attribute but found ${htmlElement::class}")
                                        return htmlElement
                                    }
                                }

                                "de.selfmade4u.tucanpluskmp.BaseContentScope.extractText" -> {
                                    if (htmlElement is HtmlRawTextImpl || htmlElement is XmlText) {
                                        return htmlElement.nextInterestingSibling
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
                var htmlElement: XmlElement? = htmlElement
                for (entry in expression.entries) {
                    entry.expression?.let {
                        htmlElement = checkExpression(annotations, it, htmlElement!!)
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

    data class AnnotationResult(val message: String, val quickFix: PsiUpdateModCommandAction<*>? = null)

    // https://github.com/JetBrains/intellij-community/blob/b926099be855e2e1c34d21df1e496f29ecbe7f52/platform/core-impl/src/com/intellij/util/CachedValueStabilityChecker.java#L62
    fun myFun(
        project: Project,
        annotationEntry: KtAnnotationEntry
    ): CachedValueProvider.Result<MutableMap<PsiElement, AnnotationResult>> {
        val annotations = mutableMapOf<PsiElement, AnnotationResult>()
        val valueArg = annotationEntry.valueArgumentList!!.arguments.first()
        val path = getStringLiteral(valueArg.stringTemplateExpression!!)
        thisLogger().warn("project dir ${project.guessProjectDir()} path $path");
        thisLogger().warn("htmls ${project.guessProjectDir()!!.findDirectory(path)}")
        val htmls = project.guessProjectDir()!!.findDirectory(path)!! // TODO FIXME error handling

        val ktNamedFunction = annotationEntry.getParentOfType<KtNamedFunction>(strict = true)!!
        //println("abc ${ktNamedFunction.text}")
        val block = ktNamedFunction.bodyBlockExpression!!

        val files = htmls.children
        thisLogger().warn("htmls2 ${files.map { (it.findPsiFile(project) as XmlFile).rootTag }}")
        val htmlFiles = files.map { (it.findPsiFile(project) as XmlFile).rootTag!! }

        //println("$htmlFiles")
        for (htmlFile in htmlFiles) {
            val parsedUntil = checkExpression(annotations, block, htmlFile)
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

// we probably want to be able to calculate how far the parsing code is executed for each html file
// the one that goes the shortest needs fixing at that place in the source code

// some change examples then can be tested against all files and check whether we make more progress in the parsing code
// and if we do there if we make more progress in the html code? maybe this is a 1:1 mapping if we count loops.
