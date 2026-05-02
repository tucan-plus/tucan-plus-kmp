package de.selfmade4u

import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import com.intellij.psi.util.CachedValueProvider
import org.jetbrains.kotlin.analysis.api.KaExperimentalApi
import org.jetbrains.kotlin.analysis.api.analyze
import org.jetbrains.kotlin.analysis.api.symbols.KaCallableSymbol
import org.jetbrains.kotlin.idea.references.mainReference
import org.jetbrains.kotlin.psi.KtAnnotationEntry
import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.psi.KtConstantExpression
import org.jetbrains.kotlin.psi.KtExpression
import org.jetbrains.kotlin.psi.KtNameReferenceExpression
import org.jetbrains.kotlin.psi.KtNamedFunction
import org.jetbrains.kotlin.psi.KtProperty
import org.jetbrains.kotlin.psi.KtReferenceExpression
import org.jetbrains.kotlin.psi.KtStringTemplateExpression
import org.jetbrains.kotlin.psi.psiUtil.getParentOfType

class Extractor {

    @OptIn(KaExperimentalApi::class)
    fun checkExpression(annotations: MutableList<PsiElement>, expression: KtExpression) {
        //println("statement ${statement.text}")
        // https://kotlin.github.io/analysis-api/fundamentals.html#kalifetimeowner
        when (expression) {
            is KtCallExpression -> {
                println("args ${expression.valueArguments}")
                analyze(expression) {
                    val symbol = expression.mainReference.resolveToSymbol() as? KaCallableSymbol
                    println(symbol)
                    val callInfo = expression.resolveCall()
                    println(callInfo)
                }
                println(expression.mainReference)
                println(expression.calleeExpression!!::class)
                when (expression.calleeExpression) {
                    is KtReferenceExpression -> {
                        println("calling ")
                    }
                }
            }

            is KtProperty -> {
                println("got a property, need to check what it gets assigned")
                val initializer = expression.initializer
                println("initializer $initializer")
                checkExpression(annotations, initializer!!)
            }

            is KtConstantExpression -> {
                // fine
            }

            is KtNameReferenceExpression -> {
                // variable reference
                // maybe KtSimpleNameExpression
            }

            else -> {
                annotations.add(expression)
            }
        }
    }

    // https://github.com/JetBrains/intellij-community/blob/b926099be855e2e1c34d21df1e496f29ecbe7f52/platform/core-impl/src/com/intellij/util/CachedValueStabilityChecker.java#L62
    fun myFun(
        project: Project,
        annotationEntry: KtAnnotationEntry
    ): CachedValueProvider.Result<MutableList<PsiElement>> {
        val annotations = mutableListOf<PsiElement>()
        val valueArg = annotationEntry.valueArgumentList!!.arguments.first()
        val text = valueArg.getArgumentExpression() as KtStringTemplateExpression
        val path = text.entries.first().text

        val ktNamedFunction = annotationEntry.getParentOfType<KtNamedFunction>(strict = true)!!
        //println("abc ${ktNamedFunction.text}")
        val block = ktNamedFunction.bodyBlockExpression!!
        for (statement in block.statements) {
            checkExpression(annotations, statement)
        }

        return CachedValueProvider.Result(annotations, annotationEntry)
    }
}