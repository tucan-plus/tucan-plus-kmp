package de.selfmade4u

import com.intellij.lang.annotation.AnnotationHolder
import com.intellij.lang.annotation.HighlightSeverity
import com.intellij.modcommand.ActionContext
import com.intellij.modcommand.ModPsiUpdater
import com.intellij.modcommand.Presentation
import com.intellij.modcommand.PsiUpdateModCommandAction
import com.intellij.openapi.project.Project
import com.intellij.openapi.project.guessProjectDir
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.openapi.vfs.findPsiFile
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiNamedElement
import com.intellij.psi.xml.XmlFile
import org.jetbrains.kotlin.idea.base.util.projectScope
import org.jetbrains.kotlin.idea.stubindex.KotlinAnnotationsIndex
import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.psi.KtIfExpression
import org.jetbrains.kotlin.psi.KtNamedFunction
import org.jetbrains.kotlin.psi.KtStringTemplateExpression
import org.jetbrains.kotlin.psi.psiUtil.getParentOfType

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

    fun process(project: Project, annotationContext: PsiElement?, holder: AnnotationHolder?) {
        val keys = KotlinAnnotationsIndex.getAllKeys(project)
        val annotations = KotlinAnnotationsIndex["HtmlFromResources", project, project.projectScope()];
        println("annotations $annotations")
        for (annotationEntry in annotations) {
            val valueArg = annotationEntry.valueArgumentList!!.arguments.first()
            val text = valueArg.getArgumentExpression() as KtStringTemplateExpression
            val path = text.entries.first().text
            val ktNamedFunction = annotationEntry.getParentOfType<KtNamedFunction>(strict = true)!!
            //println("abc ${ktNamedFunction.text}")
            val block = ktNamedFunction.bodyBlockExpression!!
            for (statement in block.statements) {
                println("statement ${statement.text}")
                // https://kotlin.github.io/analysis-api/fundamentals.html#kalifetimeowner
                when (statement) {
                    is KtCallExpression -> {
                        val args = statement.valueArgumentList
                        println("args $args")
                    }

                    is KtIfExpression -> {
                        println("some if")
                    }

                    else -> {
                        if (statement == annotationContext) {
                            holder?.newAnnotation(HighlightSeverity.ERROR, "Unknown HTML parser statement")?.range(statement)
                                ?.withFix(MyQuickFix(statement))?.create()
                        }
                    }
                }
            }

            println("path ${path}")
            val htmls = project.guessProjectDir()!!.findFileByRelativePath(path)!!
            magicFunction(htmls, project, annotationContext, holder)
        }
    }
}

// https://platform.jetbrains.com/t/displaying-custom-problems-in-the-problems-tool-window/954/5
fun magicFunction(
    directory: VirtualFile,
    project: Project,
    annotationContext: PsiElement?, holder: AnnotationHolder?
) {
    val files = directory.children
    println("files $files")
    var tags = files.map { (it.findPsiFile(project) as XmlFile).rootTag!! }
    if (tags.all { it.name == tags[0].name }) {
        tags = tags.mapNotNull { it.subTags.firstOrNull() }
        // TODO quickfix to kotlin file
    } else {
        tags.forEach { tag ->
            if (tag == annotationContext) {
                holder?.newAnnotation(HighlightSeverity.ERROR, "Different tags at same position")?.range(tag)
                    ?.create()
            }
        }
    }
}