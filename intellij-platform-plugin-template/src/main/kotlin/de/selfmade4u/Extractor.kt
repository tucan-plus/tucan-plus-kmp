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
import com.intellij.psi.PsiNamedElement
import com.intellij.psi.util.CachedValueProvider
import com.intellij.psi.util.CachedValuesManager
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.psi.util.PsiUtil
import com.intellij.psi.xml.XmlAttribute
import com.intellij.psi.xml.XmlElement
import com.intellij.psi.xml.XmlFile
import com.intellij.psi.xml.XmlProlog
import com.intellij.psi.xml.XmlTag
import org.jetbrains.kotlin.analysis.api.analyze
import org.jetbrains.kotlin.analysis.api.resolution.KaCallInfo
import org.jetbrains.kotlin.analysis.api.resolution.KaFunctionCall
import org.jetbrains.kotlin.analysis.api.resolution.KaSuccessCallInfo
import org.jetbrains.kotlin.analysis.api.resolution.symbol
import org.jetbrains.kotlin.idea.base.util.projectScope
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
import org.jetbrains.kotlin.psi.KtStringTemplateExpression
import org.jetbrains.kotlin.psi.psiUtil.getChildOfType
import org.jetbrains.kotlin.psi.psiUtil.getNextSiblingIgnoringWhitespaceAndComments
import org.jetbrains.kotlin.psi.psiUtil.getParentOfType

// https://github.com/JetBrains/kotlin/blob/master/analysis/docs/contribution-guide/api-development.md
// https://youtrack.jetbrains.com/issue/KT-61404/Analysis-API-implement-proper-library-publishing-structure
// lots of moving parts (assuming this work ever gets done)

// https://github.com/JetBrains/intellij-community/blob/37b233a6b70fb606dad5f13eb001c63fb6b12cf4/plugins/kotlin/base/analysis/src/org/jetbrains/kotlin/idea/stubindex/IdeStubIndexService.java#L306
// https://github.com/JetBrains/kotlin/blob/a66400f13ad48df4ed889ba98d94c0ece05d9acf/compiler/psi/psi-impl/src/org/jetbrains/kotlin/psi/stubs/elements/StubIndexService.kt#L12

// my first task should be to extract the information from the kotlin html parsing code.
// then we can try to generate autofixes in all directions

class MyQuickFix(element: PsiElement) : PsiUpdateModCommandAction<PsiElement>(element) {

    override fun getFamilyName(): String = "My Plugin Fixes"

    override fun getPresentation(context: ActionContext, element: PsiElement): Presentation? {
        return Presentation.of("Rename to 'UpdatedName'")
    }

    override fun invoke(context: ActionContext, element: PsiElement, updater: ModPsiUpdater) {
        // You don't need to wrap this in a WriteAction.
        // Use 'updater' to manage the PSI change.
        if (element is PsiNamedElement) {
            element.setName("UpdatedName")
        }
    }
}

class Extractor {

    fun checkExpression(annotations: MutableMap<PsiElement, String>, expression: KtExpression, htmlElement: XmlElement): XmlElement {
        //println("statement ${statement.text}")
        // https://kotlin.github.io/analysis-api/fundamentals.html#kalifetimeowner
        when (expression) {
            is KtLambdaExpression -> {
                checkExpression(annotations, expression.bodyExpression!!, htmlElement)
            }
            is KtBlockExpression -> {
                var htmlTag = htmlElement
                for (statement in expression.statements) {
                    htmlTag = checkExpression(annotations, statement, htmlTag)
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
                                    annotations[expression] = "Deprecated. Doctype does not need to be parsed."
                                    /*if (htmlElement is XmlProlog && htmlElement.doctype != null) {
                                        println("matched doctype")
                                        for (arg in expression.valueArguments) {
                                            val next = PsiTreeUtil.findChildOfAnyType<XmlElement>(htmlElement, XmlTag::class.java)
                                            checkExpression(annotations, arg.getArgumentExpression()!!, htmlElement.doctype.getChildOfType<XmlElement>()!!)
                                        }
                                        return htmlElement.getNextSiblingIgnoringWhitespaceAndComments() as XmlElement
                                    } else {
                                        annotations[expression] = "expected <doctype> but found ${htmlElement::class}"
                                    }*/
                                }
                                "de.selfmade4u.tucanpluskmp.HtmlTag.attribute" -> {
                                    if (htmlElement is XmlAttribute) {
                                        println("matched attribute")
                                        // TODO check key and value
                                        return htmlElement.getNextSiblingIgnoringWhitespaceAndComments() as XmlElement
                                    } else {
                                        annotations[expression] = "expected attribute but found ${htmlElement::class}"
                                    }
                                }
                                else -> {
                                    val implementation = psi as? KtFunction

                                    if (implementation != null) {
                                        println("Implementation found: ${implementation.fqName}")
                                        annotations[expression] = "TOOD implementation needs analysis"
                                    } else {
                                        annotations[expression] = "unknown called method"
                                    }
                                }
                            }
                        }
                        else -> {
                            annotations[expression] = "failed to resolve"
                        }
                    }
                }
            }
            is KtProperty -> {
                val initializer = expression.initializer
                checkExpression(annotations, initializer!!, htmlElement)
            }
            is KtStringTemplateExpression -> {
                for (entry in expression.entries) {
                    entry.expression?.let { checkExpression(annotations, it, htmlElement) }
                }
            }
            is KtConstantExpression -> {
                // fine
            }
            is KtNameReferenceExpression -> {
                // variable reference
                // maybe KtSimpleNameExpression
            }
            else -> {
                annotations[expression] = "Unknown HTML parser statement ${expression::class}"
            }
        }
        // TODO FIXME
        return htmlElement
    }

    // https://github.com/JetBrains/intellij-community/blob/b926099be855e2e1c34d21df1e496f29ecbe7f52/platform/core-impl/src/com/intellij/util/CachedValueStabilityChecker.java#L62
    fun myFun(project: Project, annotationEntry: KtAnnotationEntry): CachedValueProvider.Result<MutableMap<PsiElement, String>> {
        val annotations = mutableMapOf<PsiElement, String>()
        val valueArg = annotationEntry.valueArgumentList!!.arguments.first()
        val text = valueArg.getArgumentExpression() as KtStringTemplateExpression
        val path = text.entries.first().text
        val htmls = project.guessProjectDir()!!.findFileByRelativePath(path)!!

        val ktNamedFunction = annotationEntry.getParentOfType<KtNamedFunction>(strict = true)!!
        //println("abc ${ktNamedFunction.text}")
        val block = ktNamedFunction.bodyBlockExpression!!

        val files = htmls.children
        val htmlFiles = files.map { (it.findPsiFile(project) as XmlFile).rootTag!! }

        println("$htmlFiles")
        for (htmlFile in htmlFiles) {
            // TODO here we should start parsing htmlTag
            checkExpression(annotations, block, htmlFile)
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
                annotations[annotationContext]?.let { holder.newAnnotation(HighlightSeverity.ERROR, it).range(annotationContext).create() }
                // .withFix(MyQuickFix(annotation)
            }
        }
    }
}

// we probably want to be able to calculate how far the parsing code is executed for each html file
// the one that goes the shortest needs fixing at that place in the source code

// some change examples then can be tested against all files and check whether we make more progress in the parsing code
// and if we do there if we make more progress in the html code? maybe this is a 1:1 mapping if we count loops.
